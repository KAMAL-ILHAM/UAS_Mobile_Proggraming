package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuatLaporanKonfirmasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_konfirmasi)

        // 1. Kenalkan Views (Sesuai dengan XML yang kamu berikan)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        // --- DATA FOTO & TANGGAL ---
        val ivPreviewKonfirmasi = findViewById<ImageView>(R.id.ivPreviewKonfirmasi)
        // Catatan: Di XML, teks tanggal "LIVE CAPTURE..." tidak punya ID.
        // Jadi kita ambil LinearLayout pembungkusnya dulu, lalu ambil anak TextView-nya.
        val dateContainer = (ivPreviewKonfirmasi.parent as androidx.constraintlayout.widget.ConstraintLayout).getChildAt(1) as android.widget.LinearLayout
        val tvTanggal = dateContainer.getChildAt(1) as TextView

        // --- DATA KATEGORI ---
        // 1. Ambil Root Layout (ConstraintLayout) dari Activity
        val rootView = findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0) as android.view.ViewGroup

        // 2. Ambil NestedScrollView (berada di urutan ke-3 dari atas, alias index 2)
        val mainLayout = rootView.getChildAt(2) as androidx.core.widget.NestedScrollView

        // 3. Masuk ke dalam CardView
        val linearDalamScroll = mainLayout.getChildAt(0) as android.widget.LinearLayout
        val cardView = linearDalamScroll.getChildAt(2) as androidx.cardview.widget.CardView
        val innerCardLayout = cardView.getChildAt(0) as android.widget.LinearLayout

        // 4. Ambil TextView Kategori
        val kategoriLayout = (innerCardLayout.getChildAt(1) as android.widget.LinearLayout).getChildAt(1) as android.widget.LinearLayout
        val tvKategori = kategoriLayout.getChildAt(1) as TextView
        // --- DATA LOKASI ---
        val lokasiLayout = (innerCardLayout.getChildAt(3) as android.widget.LinearLayout).getChildAt(1) as android.widget.LinearLayout
        val tvLokasi = lokasiLayout.getChildAt(1) as TextView

        // --- DATA DESKRIPSI ---
        val tvDeskripsiKonfirmasi = findViewById<TextView>(R.id.tvDeskripsiKonfirmasi)

        // --- TOMBOL KIRIM ---
        val btnKirimLaporan = findViewById<MaterialButton>(R.id.btnKirimLaporan)

        // 2. Tangkap data dari Halaman Sebelumnya
        val kategori = intent.getStringExtra("KATEGORI_FINAL") ?: "Kategori Tidak Diketahui"
        val gedung = intent.getStringExtra("GEDUNG_FINAL") ?: ""
        val ruangan = intent.getStringExtra("RUANGAN_FINAL") ?: ""
        val detail = intent.getStringExtra("DETAIL_FINAL") ?: "Tidak ada deskripsi."
        val fotoUriString = intent.getStringExtra("FOTO_URI") ?: ""

        // 3. Suntikkan Data Teks ke UI
        tvKategori.text = kategori
        tvLokasi.text = "$gedung, $ruangan"
        tvDeskripsiKonfirmasi.text = detail

        // 4. Ubah URI String menjadi Gambar Asli dan tampilkan
        if (fotoUriString.isNotEmpty()) {
            val imageUri = Uri.parse(fotoUriString)
            ivPreviewKonfirmasi.setImageURI(imageUri)
        }

        // 5. Generate Tanggal Real-Time secara Otomatis
        val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID")) // Format: 24 Apr 09:42
        val currentDate = dateFormat.format(Date())
        tvTanggal.text = "LIVE CAPTURE: ${currentDate.uppercase()}"

        // 6. Fungsi Tombol Back
        btnBack.setOnClickListener { finish() }

        // 7. Fungsi Klik Tombol Kirim (Simulasi Animasi & State Lokal)
        btnKirimLaporan.setOnClickListener {
            btnKirimLaporan.isEnabled = false
            btnKirimLaporan.text = "Mengirim Laporan..."
            btnKirimLaporan.icon = null

            // Simulasi proses pengiriman data API selama 2 detik
            Handler(Looper.getMainLooper()).postDelayed({
                val sharedPref = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("kategori", kategori)
                    putString("lokasi", "$gedung, $ruangan")
                    putString("status", "● SEDANG DIPROSES")
                    putString("detail", detail)
                    apply()
                }

                // Arahkan ke Halaman Sukses
                val successIntent = Intent(this, LaporanSuksesActivity::class.java)
                successIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(successIntent)
                finish()
            }, 2000)
        }
    }
}