package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.content.Context

class BuatLaporanKonfirmasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_konfirmasi)

        // 1. Fungsi Tombol Tutup/Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 2. Tangkap semua data yang dilempar dari Langkah 2
        val kategori = intent.getStringExtra("KATEGORI_FINAL") ?: ""
        val gedung = intent.getStringExtra("GEDUNG_FINAL") ?: ""
        val ruangan = intent.getStringExtra("RUANGAN_FINAL") ?: ""
        val detail = intent.getStringExtra("DETAIL_FINAL") ?: ""
        val fotoUri = intent.getStringExtra("FOTO_URI") ?: ""

        // 3. Fungsi Klik Tombol Kirim (Simulasi Animasi & State)
        val btnKirimLaporan = findViewById<MaterialButton>(R.id.btnKirimLaporan)

        btnKirimLaporan.setOnClickListener {
            // Mencegah double-click spam
            btnKirimLaporan.isEnabled = false

            // Ubah teks dan ikon menjadi proses loading
            btnKirimLaporan.text = "Mengirim Laporan..."
            btnKirimLaporan.icon = null // Sembunyikan icon send saat loading

            // ==========================================
            // TODO untuk Tim Database:
            // Silakan integrasikan dengan Firebase Firestore & Storage.
            // Variabel data yang siap dikirim:
            // 1. kategori
            // 2. gedung
            // 3. ruangan
            // 4. detail
            // 5. fotoUri  <-- Alamat file fisik foto (String)
            // ==========================================

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

                // Setelah 2 detik, arahkan ke Halaman Sukses
                val intent = Intent(this, LaporanSuksesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            }, 2000)
        }
    }
}