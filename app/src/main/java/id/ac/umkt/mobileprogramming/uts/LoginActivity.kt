package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Mengenalkan elemen UI dari XML ke Kotlin
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val etNim: TextInputEditText = findViewById(R.id.etNim)
        val etPassword: TextInputEditText = findViewById(R.id.etPassword)
        val btnLogin: MaterialButton = findViewById(R.id.btnLogin)
        val tvRegister: TextView = findViewById(R.id.tvRegister) // SUDAH DIPERBAIKI DISINI
        val tvForgotPassword: TextView = findViewById(R.id.tvForgotPassword)

        // 1. Aksi saat tombol panah kembali ditekan
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Beri perintah saat tombol ditekan
        btnLogin.setOnClickListener {
            // Perintah untuk pindah dari LoginActivity ke MainActivity (Dashboard)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Tambahkan finish() agar saat di Dashboard, user tidak bisa
            // menekan tombol back untuk kembali ke layar Login lagi
            finish()
        }

        // 3. Aksi saat tulisan "Buat Akun" ditekan (SUDAH DIPERBAIKI DISINI)
        tvRegister.setOnClickListener {
            // Berpindah ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 4. Aksi saat tulisan "Lupa kata sandi?" ditekan
        tvForgotPassword.setOnClickListener {
            // Membuka LupaPasswordFragment
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, LupaPasswordFragment())
                .addToBackStack(null) // Memungkinkan user menekan tombol 'Back' di HP untuk kembali ke Login
                .commit()
        }
    }
}