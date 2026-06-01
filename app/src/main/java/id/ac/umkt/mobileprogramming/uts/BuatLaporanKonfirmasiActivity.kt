package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuatLaporanKonfirmasiActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_konfirmasi)

        db = FirebaseFirestore.getInstance()

        // --- KENALKAN VIEWS ---
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val ivPreviewKonfirmasi = findViewById<ImageView>(R.id.ivPreviewKonfirmasi)

        val dateContainer = (ivPreviewKonfirmasi.parent as androidx.constraintlayout.widget.ConstraintLayout).getChildAt(1) as android.widget.LinearLayout
        val tvTanggal = dateContainer.getChildAt(1) as TextView

        val rootView = findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0) as android.view.ViewGroup
        val mainLayout = rootView.getChildAt(2) as androidx.core.widget.NestedScrollView
        val linearDalamScroll = mainLayout.getChildAt(0) as android.widget.LinearLayout
        val cardView = linearDalamScroll.getChildAt(2) as androidx.cardview.widget.CardView
        val innerCardLayout = cardView.getChildAt(0) as android.widget.LinearLayout

        val kategoriLayout = (innerCardLayout.getChildAt(1) as android.widget.LinearLayout).getChildAt(1) as android.widget.LinearLayout
        val tvKategori = kategoriLayout.getChildAt(1) as TextView

        val lokasiLayout = (innerCardLayout.getChildAt(3) as android.widget.LinearLayout).getChildAt(1) as android.widget.LinearLayout
        val tvLokasi = lokasiLayout.getChildAt(1) as TextView

        val tvDeskripsiKonfirmasi = findViewById<TextView>(R.id.tvDeskripsiKonfirmasi)
        val btnKirimLaporan = findViewById<MaterialButton>(R.id.btnKirimLaporan)

        // --- TANGKAP DATA DARI HALAMAN SEBELUMNYA ---
        val kategori = intent.getStringExtra("KATEGORI_FINAL") ?: "Kategori Tidak Diketahui"
        val gedung = intent.getStringExtra("GEDUNG_FINAL") ?: ""
        val ruangan = intent.getStringExtra("RUANGAN_FINAL") ?: ""
        val detail = intent.getStringExtra("DETAIL_FINAL") ?: "Tidak ada deskripsi."
        val fotoUriString = intent.getStringExtra("FOTO_URI") ?: ""

        // --- AMBIL NAMA USER DARI SESSION (TANDA PENGENAL) ---
        val sharedPrefUser = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val namaUserSaatIni = sharedPrefUser.getString("full_name", "User Anonim") ?: "User Anonim"

        // --- SUNTIKKAN DATA KE UI ---
        tvKategori.text = kategori
        tvLokasi.text = "$gedung, $ruangan"
        tvDeskripsiKonfirmasi.text = detail

        if (fotoUriString.isNotEmpty()) {
            val imageUri = Uri.parse(fotoUriString)
            ivPreviewKonfirmasi.setImageURI(imageUri)
        }

        val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        tvTanggal.text = "LIVE CAPTURE: ${currentDate.uppercase()}"

        btnBack.setOnClickListener { finish() }

        // --- PROSES UPLOAD FIREBASE ---
        btnKirimLaporan.setOnClickListener {
            btnKirimLaporan.isEnabled = false
            btnKirimLaporan.text = "Mengirim Laporan..."
            btnKirimLaporan.icon = null

            // 1. Cek jumlah laporan dengan kategori yang sama di Firebase
            db.collection("laporan")
                .whereEqualTo("kategori", kategori)
                .get()
                .addOnSuccessListener { snapshot ->

                    // Hitung urutan laporan untuk membuat nomor tiket kustom
                    val urutanNext = snapshot.size() + 1
                    val formatUrutan = String.format("%04d", urutanNext)
                    val kodeKategori = getKategoriCode(kategori)
                    val nomorTiketKustom = "$kodeKategori-$formatUrutan"

                    // Proses konversi file gambar (Sekarang dengan Resize Otomatis)
                    val base64Gambar = konversiUriKeBase64(fotoUriString)

                    if (base64Gambar.isEmpty() && fotoUriString.isNotEmpty()) {
                        Toast.makeText(this@BuatLaporanKonfirmasiActivity, "Gagal memproses gambar, silakan foto kembali.", Toast.LENGTH_SHORT).show()
                        resetTombolKirim(btnKirimLaporan)
                        return@addOnSuccessListener
                    }

                    // Susun data untuk dikirim ke Firebase
                    val dataLaporan = hashMapOf(
                        "nomor_tiket" to nomorTiketKustom,
                        "kategori" to kategori,
                        "lokasi" to "$gedung, $ruangan",
                        "detail" to detail,
                        "foto_base64" to base64Gambar,
                        "status" to "● SEDANG DIPROSES",
                        "nama_pelapor" to namaUserSaatIni,
                        "timestamp" to Timestamp.now()
                    )

                    // Simpan data ke Firebase
                    db.collection("laporan")
                        .add(dataLaporan)
                        .addOnSuccessListener { documentReference ->
                            // Simpan backup lokal untuk keperluan tampilan cepat
                            val sharedPref = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("kategori", kategori)
                                putString("lokasi", "$gedung, $ruangan")
                                putString("status", "● SEDANG DIPROSES")
                                putString("detail", detail)
                                apply()
                            }

                            // Pindah ke Halaman Sukses
                            val intentSukses = Intent(this@BuatLaporanKonfirmasiActivity, LaporanSuksesActivity::class.java)
                            intentSukses.putExtra("NOMOR_TIKET", nomorTiketKustom)
                            intentSukses.putExtra("DOCUMENT_ID", documentReference.id)
                            intentSukses.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentSukses)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            resetTombolKirim(btnKirimLaporan)
                            Toast.makeText(this@BuatLaporanKonfirmasiActivity, "Gagal menyimpan: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { exception ->
                    resetTombolKirim(btnKirimLaporan)
                    Toast.makeText(this@BuatLaporanKonfirmasiActivity, "Gagal memvalidasi antrean: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    /**
     * Mengubah nama kategori menjadi kode unik untuk nomor tiket
     */
    private fun getKategoriCode(kategori: String): String {
        return when (kategori.lowercase(Locale.ROOT).trim()) {
            "furnitur" -> "01"
            "elektronik" -> "02"
            "sanitasi" -> "03"
            "jaringan" -> "04"
            "gedung" -> "05"
            "area luar" -> "06"
            else -> "00"
        }
    }

    /**
     * Mengubah file gambar menjadi teks Base64 sekaligus memperkecil resolusi (Resize)
     * agar ukurannya aman dari limit 1MB Firestore.
     */
    private fun konversiUriKeBase64(uriString: String): String {
        if (uriString.isEmpty()) return ""
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return ""

            // --- FITUR RESIZE (Maksimal 800 piksel) ---
            val maxImageSize = 800f
            val ratio = Math.min(
                maxImageSize / originalBitmap.width,
                maxImageSize / originalBitmap.height
            )
            val width = Math.round(ratio * originalBitmap.width)
            val height = Math.round(ratio * originalBitmap.height)

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)

            // --- PROSES KOMPRESI ---
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()

            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun resetTombolKirim(btn: MaterialButton) {
        btn.isEnabled = true
        btn.text = "Kirim Laporan"
    }
}