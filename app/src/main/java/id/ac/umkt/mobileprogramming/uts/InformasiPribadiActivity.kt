package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class InformasiPribadiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi_pribadi)

        // 1. Kenalkan Views
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnUbahFoto = findViewById<TextView>(R.id.btnUbahFoto)
        val btnSimpan = findViewById<MaterialButton>(R.id.btnSimpan)
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)
        val avatarContainer = findViewById<FrameLayout>(R.id.avatarContainer)

        // 2. Terapkan Animasi Masuk (Premium Startup Feel)
        setupEntranceAnimations(layoutMainContent, avatarContainer)

        // 3. Fungsi Tombol Back
        btnBack.setOnClickListener {
            finish()
        }

        // 4. Fungsi Ubah Foto Profil
        btnUbahFoto.setOnClickListener {
            Toast.makeText(this, "Membuka galeri...", Toast.LENGTH_SHORT).show()
            // Logika image picker bisa ditaruh di sini nanti
        }

        // 5. Fungsi Simpan Perubahan
        btnSimpan.setOnClickListener {
            // Cegah double-click
            btnSimpan.isEnabled = false
            btnSimpan.text = "Menyimpan..."
            btnSimpan.icon = null // Sembunyikan icon save sementara

            // Simulasi API Request (1.5 detik)
            Handler(Looper.getMainLooper()).postDelayed({

                // Kembalikan state tombol
                btnSimpan.text = "Tersimpan!"
                btnSimpan.setBackgroundColor(android.graphics.Color.parseColor("#10B981")) // Ubah jadi hijau

                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()

                // Kembalikan ke normal setelah 1 detik
                Handler(Looper.getMainLooper()).postDelayed({
                    finish() // Tutup halaman setelah sukses
                }, 1000)

            }, 1500)
        }
    }

    private fun setupEntranceAnimations(contentView: LinearLayout, avatarView: FrameLayout) {
        // Animasi Slide Up untuk Form
        contentView.translationY = 80f
        contentView.alpha = 0f
        contentView.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animasi Bounce untuk Foto Profil
        avatarView.scaleX = 0f
        avatarView.scaleY = 0f
        avatarView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setStartDelay(100) // Sedikit ditunda agar bergantian dengan form
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }
}