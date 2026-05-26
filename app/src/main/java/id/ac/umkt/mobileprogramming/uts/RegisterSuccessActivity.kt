package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. PASANG LAYOUT XML TERLEBIH DAHULU (Pindah ke sini)
        setContentView(R.layout.activity_register_success)

        // 2. BARU ATUR FULLSCREEN EDGE-TO-EDGE
        window.setDecorFitsSystemWindows(false)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        // 3. Ambil Data Nama dari Intent
        val registeredName = intent.getStringExtra("USER_NAME") ?: "KAMAL ILHAM"
        findViewById<TextView>(R.id.tvRegisteredName).text = registeredName.uppercase()

        // 4. Kenalkan Views
        val layoutAnimatedContent = findViewById<LinearLayout>(R.id.layoutAnimatedContent)
        val btnMasukDashboard = findViewById<MaterialButton>(R.id.btnMasukDashboard)

        // 5. Jalankan Animasi Masuk
        setupEntranceAnimations(layoutAnimatedContent, btnMasukDashboard)

        // 6. Interaksi Tombol Lanjut
        btnMasukDashboard.setOnClickListener {
            btnMasukDashboard.isEnabled = false

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun setupEntranceAnimations(contentView: LinearLayout, buttonView: MaterialButton) {
        // State awal: Tersembunyi dan bergeser ke bawah
        contentView.alpha = 0f
        contentView.translationY = 100f

        buttonView.alpha = 0f
        buttonView.translationY = 100f

        // Animasi Konten Tengah
        contentView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(150) // Jeda sedikit saat activity baru dirender
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()

        // Animasi Tombol Bawah (Muncul sedikit lebih lambat dari konten)
        buttonView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(300)
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()
    }
}