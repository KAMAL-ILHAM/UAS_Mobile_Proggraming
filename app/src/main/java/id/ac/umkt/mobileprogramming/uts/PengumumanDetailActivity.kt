package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class PengumumanDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengumuman_detail)

        // 1. Kenalkan View
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTutup = findViewById<MaterialButton>(R.id.btnTutup)

        // 2. Animasi Premium Saat Halaman Dibuka (Slide up & Fade in)
        setupEntranceAnimation(layoutMainContent)

        // 3. Tombol Kembali di Header
        btnBack.setOnClickListener {
            finish()
        }

        // 4. Tombol "Mengerti, Tutup" di Bawah
        btnTutup.setOnClickListener {
            // Mencegah double klik
            btnTutup.isEnabled = false

            // Catatan: Jika ada database, di sini kamu ubah status notifikasi menjadi "Sudah Dibaca"

            finish() // Tutup halaman
        }
    }

    private fun setupEntranceAnimation(contentView: LinearLayout) {
        // Set posisi konten agak ke bawah dan tidak terlihat
        contentView.translationY = 100f
        contentView.alpha = 0f

        // Animasikan konten naik ke atas dan muncul (fade in)
        contentView.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(500) // Durasi setengah detik
            .setInterpolator(DecelerateInterpolator()) // Efek melambat halus di akhir
            .setStartDelay(100)
            .start()
    }
}