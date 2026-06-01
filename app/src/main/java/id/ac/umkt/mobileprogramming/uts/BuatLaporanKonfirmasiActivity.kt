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

    // 1. Deklarasikan Firebase Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_konfirmasi)

        // 2. Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        // --- AMBIL DAN KENALKAN VIEWS (MEMPERTAHANKAN LOGIKA ASLI KAMU) ---
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val ivPreviewKonfirmasi = findViewById<ImageView>(R.id.ivPreviewKonfirmasi)

        // Ambil penampung tanggal
        val dateContainer = (ivPreviewKonfirmasi.parent as androidx.constraintlayout.widget.ConstraintLayout).getChildAt(1) as android.widget.LinearLayout
        val tvTanggal = dateContainer.getChildAt(1) as TextView

        // Ambil elemen Kategori & Lokasi lewat susunan hierarki layout aslimu
        val rootView = findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0) as android.view.ViewGroup
        val mainLayout = rootView.getChildAt(2) as androidx.core.widget.NestedScrollView
        val linearDalamScroll = mainLayout.getChildAt(0) as android.widget.LinearLayout
        val cardView = linearDalamScroll.getChildAt(2) as androidx.cardview.widget.CardView
        val innerCardLayout = cardView.getChildAt(0) as android.widget.LinearLayout

        // TextView Kategori
        val kategoriLayout = (innerCardLayout.getChildAt(1) as android.widget.LinearLayout).getChildAt(1) as android.widget.LinearLayout
        val tvKategori = kategoriLayout.getChildAt(1) as TextView

        // TextView Lokasi
        val lokasiLayout = (innerCardLayout.getChildAt(3) as android.widget.LinearLayout).getChildAt(1) as android.widget.LinearLayout
        val tvLokasi = lokasiLayout.getChildAt(1) as TextView

        // TextView Deskripsi
        val tvDeskripsiKonfirmasi = findViewById<TextView>(R.id.tvDeskripsiKonfirmasi)

        // Tombol Kirim
        val btnKirimLaporan = findViewById<MaterialButton>(R.id.btnKirimLaporan)

        // --- TANGKAP DATA DARI HALAMAN SEBELUMNYA ---
        val kategori = intent.getStringExtra("KATEGORI_FINAL") ?: "Kategori Tidak Diketahui"
        val gedung = intent.getStringExtra("GEDUNG_FINAL") ?: ""
        val ruangan = intent.getStringExtra("RUANGAN_FINAL") ?: ""
        val detail = intent.getStringExtra("DETAIL_FINAL") ?: "Tidak ada deskripsi."
        val fotoUriString = intent.getStringExtra("FOTO_URI") ?: ""

        // --- SUNTIKKAN DATA TEKS KE UI ---
        tvKategori.text = kategori
        tvLokasi.text = "$gedung, $ruangan"
        tvDeskripsiKonfirmasi.text = detail

        // Tampilkan gambar pada preview
        if (fotoUriString.isNotEmpty()) {
            val imageUri = Uri.parse(fotoUriString)
            ivPreviewKonfirmasi.setImageURI(imageUri)
        }

        // Generate Tanggal Real-Time secara Otomatis
        val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        tvTanggal.text = "LIVE CAPTURE: ${currentDate.uppercase()}"

        // Fungsi Tombol Back
        btnBack.setOnClickListener { finish() }

        // --- AKSI TOMBOL KIRIM REAL KE FIREBASE ---
        btnKirimLaporan.setOnClickListener {
            // Kunci tombol agar tidak bisa di-spam klik oleh user
            btnKirimLaporan.isEnabled = false
            btnKirimLaporan.text = "Mengirim Laporan..."
            btnKirimLaporan.icon = null

            // Konversi file gambar menjadi string Base64 (Terkompresi secara aman di bawah 1MB)
            val base64Gambar = konversiUriKeBase64(fotoUriString)

            if (base64Gambar.isEmpty() && fotoUriString.isNotEmpty()) {
                Toast.makeText(this, "Gagal memproses gambar, silakan foto kembali.", Toast.LENGTH_SHORT).show()
                resetTombolKirim(btnKirimLaporan)
                return@setOnClickListener
            }

            // Susun struktur data Map untuk dikirim ke Firestore Database
            val dataLaporan = hashMapOf(
                "kategori" to kategori,
                "lokasi" to "$gedung, $ruangan",
                "detail" to detail,
                "foto_base64" to base64Gambar,
                "status" to "● SEDANG DIPROSES",
                "timestamp" to Timestamp.now()
            )

            // Tembak data langsung ke dalam koleksi "laporan" di Firebase
            db.collection("laporan")
                .add(dataLaporan)
                .addOnSuccessListener {
                    // Berhasil! Simpan cadangan data ke SharedPreferences lokal (Sesuai fungsi awalmu)
                    val sharedPref = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("kategori", kategori)
                        putString("lokasi", "$gedung, $ruangan")
                        putString("status", "● SEDANG DIPROSES")
                        putString("detail", detail)
                        apply()
                    }

                    // Pindah secara bersih ke Halaman Sukses
                    val successIntent = Intent(this, LaporanSuksesActivity::class.java)
                    successIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(successIntent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    // Jika gagal (misal internet putus), kembalikan status tombol
                    resetTombolKirim(btnKirimLaporan)
                    Toast.makeText(this, "Gagal mengunggah laporan: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    /**
     * Fungsi konversi URI gambar ke teks Base64 String yang aman untuk Firestore.
     * Menggunakan ContentResolver agar mampu membaca file cache internal Android secara akurat.
     */
    private fun konversiUriKeBase64(uriString: String): String {
        if (uriString.isEmpty()) return ""
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return ""

            // Kompresi kualitas gambar ke 65% agar menghemat ruang penyimpanan Firebase
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 65, outputStream)
            val byteArray = outputStream.toByteArray()

            // Ubah byte biner gambar menjadi teks string Base64
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // Fungsi pembantu mengembalikan desain tombol awal apabila proses upload gagal
    private fun resetTombolKirim(btn: MaterialButton) {
        btn.isEnabled = true
        btn.text = "Kirim Laporan"
    }
}