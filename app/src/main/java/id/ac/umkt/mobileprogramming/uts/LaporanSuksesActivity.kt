package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

class LaporanSuksesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_sukses)

        // 1. Inisialisasi View
        val layoutIllustration = findViewById<ConstraintLayout>(R.id.layoutIllustration)
        val btnLacakStatus = findViewById<MaterialButton>(R.id.btnLacakStatus)
        val btnKembaliBeranda = findViewById<MaterialButton>(R.id.btnKembaliBeranda)

        // 2. Setup Animasi Masuk (Premium Feel)
        setupEntranceAnimation(layoutIllustration)

        // 3. Fungsi Tombol "Lacak Status Laporan"
        btnLacakStatus.setOnClickListener {
            // Mencegah double klik
            btnLacakStatus.isEnabled = false

            // Arahkan ke halaman Detail/Tracking Laporan
            val intent = Intent(this, DetailRiwayatActivity::class.java)
            // (Opsional) Bawa data ID Tiket ke halaman selanjutnya
            // intent.putExtra("TICKET_ID", "#EIO-8822")
            startActivity(intent)
            finish()
        }

        // 4. Fungsi Tombol "Kembali ke Beranda"
        btnKembaliBeranda.setOnClickListener {
            btnKembaliBeranda.isEnabled = false

            // Kembali ke Dashboard Utama dengan membersihkan tumpukan halaman sebelumnya
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Fungsi untuk membuat elemen melompat halus (Pop-up/Scale) saat halaman dibuka
    private fun setupEntranceAnimation(illustrationView: ConstraintLayout) {
        // Set kondisi awal: Kecil (Scale 0) dan Tak terlihat (Alpha 0)
        illustrationView.alpha = 0f
        illustrationView.scaleX = 0f
        illustrationView.scaleY = 0f

        // Jalankan Animasi
        illustrationView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600) // Durasi 0.6 detik
            .setInterpolator(OvershootInterpolator(1.2f)) // Efek memantul halus di akhir
            .setStartDelay(150) // Jeda sedikit agar layar stabil dulu
            .start()
    }
}