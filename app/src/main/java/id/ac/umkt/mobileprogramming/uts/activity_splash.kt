package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth // <-- Tambahan import Firebase

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay selama 2.5 detik (2500 milidetik), lalu pindah ke halaman selanjutnya
        Handler(Looper.getMainLooper()).postDelayed({

            // --- TAMBAHAN BARU: CEK SESI LOGIN FIREBASE ---
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // Skenario 1: Sudah Login -> Langsung gas ke Beranda
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Skenario 2: Belum Login -> Tetap jalankan kode aslimu ke Onboarding
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
            }

            finish() // Menutup Splash Screen agar tidak bisa di-back
        }, 2500)
    }
}