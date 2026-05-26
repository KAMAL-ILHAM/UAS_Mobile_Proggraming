package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay selama 2.5 detik (2500 milidetik), lalu pindah ke halaman selanjutnya
        Handler(Looper.getMainLooper()).postDelayed({
            // Nanti ubah LoginActivity::class.java menjadi OnboardingActivity::class.java
            // Jika file onboarding-nya sudah kita buat di langkah berikutnya
            val intent = Intent(this, OnboardingActivity::class.java) // Ganti ke Onboarding
            startActivity(intent)
            finish() // Menutup Splash Screen agar tidak bisa di-back
        }, 2500)
    }
}