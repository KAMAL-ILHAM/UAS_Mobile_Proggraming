package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    // Variabel penghitung klik kosong
    private var emptyClickCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Kenalkan Views
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnMasuk = findViewById<MaterialButton>(R.id.btnMasuk)
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)

        val inputLayoutNim = findViewById<TextInputLayout>(R.id.inputLayoutNim)
        val etNim = findViewById<TextInputEditText>(R.id.etNim)
        val inputLayoutPassword = findViewById<TextInputLayout>(R.id.inputLayoutPassword)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        // 2. Animasi Masuk Halaman
        layoutMainContent.translationY = 80f
        layoutMainContent.alpha = 0f
        layoutMainContent.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        btnBack.setOnClickListener { finish() }

        // 4. Navigasi ke Register
        findViewById<LinearLayout>(R.id.layoutRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 5. Lupa Password
        findViewById<TextView>(R.id.tvLupaSandi).setOnClickListener {
            val lupaPasswordFragment = LupaPasswordFragment()
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, lupaPasswordFragment)
                .addToBackStack(null)
                .commit()
        }

        // 5. Tombol Masuk
        btnMasuk.setOnClickListener {
            inputLayoutNim.error = null
            inputLayoutPassword.error = null

            val nim = etNim.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // LOGIKA VALIDASI & BYPASS
            if (nim.isEmpty() || password.isEmpty()) {
                emptyClickCounter++

                if (emptyClickCounter >= 3) {
                    // BYPASS BYPASS BYPASS
                    Toast.makeText(this, "Bypass mode diaktifkan!", Toast.LENGTH_SHORT).show()
                    emptyClickCounter = 0 // Reset counter
                    doLogin("2411102441015") // Login otomatis pakai NIM ini
                } else {
                    // Peringatan normal
                    if (nim.isEmpty()) inputLayoutNim.error = "Field tidak boleh kosong"
                    if (password.isEmpty()) inputLayoutPassword.error = "Field tidak boleh kosong"
                    Toast.makeText(this, "Field tidak boleh kosong ($emptyClickCounter/3)", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            // Jika input terisi, reset counter dan proses login normal
            emptyClickCounter = 0

            // State Loading
            btnMasuk.isEnabled = false
            btnMasuk.text = "Memvalidasi..."
            btnMasuk.icon = null

            // Simulasi Request API
            Handler(Looper.getMainLooper()).postDelayed({
                if (nim == "2411102441015") {
                    doLogin(nim)
                } else {
                    btnMasuk.isEnabled = true
                    btnMasuk.text = "Masuk Sistem"
                    btnMasuk.setIconResource(R.drawable.ic_login_arrow)
                    inputLayoutNim.error = "NIM tidak terdaftar di sistem"
                }
            }, 1000)
        }
    }

    // FUNGSI BANTUAN (Helper Function) untuk Login
    private fun doLogin(nim: String) {
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("full_name", "Kamal Ilham")
            putString("nim", nim)
            putString("major", "Teknik Informatika")
            apply()
        }

        Toast.makeText(this, "Selamat datang, Kamal Ilham", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}