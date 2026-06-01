package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton

class LaporanSuksesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_sukses)

        // 1. Inisialisasi View
        val layoutIllustration = findViewById<ConstraintLayout>(R.id.layoutIllustration)
        val tvTicketBadge = findViewById<TextView>(R.id.tvTicketBadge)
        val btnLacakStatus = findViewById<MaterialButton>(R.id.btnLacakStatus)
        val btnKembaliBeranda = findViewById<MaterialButton>(R.id.btnKembaliBeranda)

        // 2. Tangkap nomor tiket kustom yang sudah diracik dari halaman konfirmasi
        val nomorTiket = intent.getStringExtra("NOMOR_TIKET") ?: "00-0000"
        val documentId = intent.getStringExtra("DOCUMENT_ID") ?: ""

        // 3. Tampilkan nomor tiket dengan format (misal: TIKET: 04-0001)
        tvTicketBadge.text = "TIKET: $nomorTiket"

        // 4. Setup Animasi Masuk
        setupEntranceAnimation(layoutIllustration)

        // 5. Fungsi Tombol "Lacak Status Laporan"
        btnLacakStatus.setOnClickListener {
            btnLacakStatus.isEnabled = false

            val intent = Intent(this, DetailRiwayatActivity::class.java)
            // Bawa DOCUMENT_ID dan NOMOR_TIKET ke halaman detail
            intent.putExtra("DOCUMENT_ID", documentId)
            intent.putExtra("NOMOR_TIKET", nomorTiket)
            startActivity(intent)
            finish()
        }

        // 6. Fungsi Tombol "Kembali ke Beranda"
        btnKembaliBeranda.setOnClickListener {
            btnKembaliBeranda.isEnabled = false

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupEntranceAnimation(illustrationView: ConstraintLayout) {
        illustrationView.alpha = 0f
        illustrationView.scaleX = 0f
        illustrationView.scaleY = 0f

        illustrationView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(OvershootInterpolator(1.2f))
            .setStartDelay(150)
            .start()
    }
}