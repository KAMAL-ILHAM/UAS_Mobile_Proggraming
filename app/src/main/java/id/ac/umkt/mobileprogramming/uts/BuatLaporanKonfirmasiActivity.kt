package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class BuatLaporanKonfirmasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_konfirmasi)

        // 1. Fungsi Tombol Tutup/Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 2. Fungsi Klik Tombol Kirim (Simulasi Animasi & State)
        val btnKirimLaporan = findViewById<MaterialButton>(R.id.btnKirimLaporan)

        btnKirimLaporan.setOnClickListener {
            // Mencegah double-click spam
            btnKirimLaporan.isEnabled = false

            // Ubah teks dan ikon menjadi proses loading
            btnKirimLaporan.text = "Mengirim Laporan..."
            btnKirimLaporan.icon = null // Sembunyikan icon send saat loading

            // Simulasi proses pengiriman data API selama 2 detik
            Handler(Looper.getMainLooper()).postDelayed({

                // Setelah 2 detik, arahkan ke Halaman Sukses
                val intent = Intent(this, LaporanSuksesActivity::class.java)

                // Flag ini membersihkan riwayat halaman agar user tidak bisa menekan "Back"
                // dan kembali ke halaman form konfirmasi ini
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()

            }, 2000) // Delay 2000 ms (2 detik)
        }
    }
}