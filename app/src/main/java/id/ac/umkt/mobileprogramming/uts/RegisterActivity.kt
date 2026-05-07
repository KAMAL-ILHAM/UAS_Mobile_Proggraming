package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat // Import ini wajib ditambahkan untuk membaca HTML
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnBack: ImageView = findViewById(R.id.btnBackRegister)
        val btnDaftar: MaterialButton = findViewById(R.id.btnDaftar)
        val tvMasukDisini: TextView = findViewById(R.id.tvMasukDisini)

        // 1. Panggil TextView Syarat dan Ketentuan
        val tvTerms: TextView = findViewById(R.id.tvTerms)

        // 2. Terjemahkan string HTML-nya ke dalam TextView
        val teksHtml = getString(R.string.teks_syarat_ketentuan)
        tvTerms.text = HtmlCompat.fromHtml(teksHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Tombol panah kembali ke layar sebelumnya
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Teks "Masuk di sini" kembali ke halaman Login
        tvMasukDisini.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tombol Daftar Sekarang
        btnDaftar.setOnClickListener {
            Toast.makeText(this, "Proses pendaftaran...", Toast.LENGTH_SHORT).show()
        }
    }
}