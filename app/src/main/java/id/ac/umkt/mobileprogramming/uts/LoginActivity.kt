package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    // Deklarasi variabel Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Variabel untuk mengecek status ikon mata (password)
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Mengenalkan elemen UI dari XML ke Kotlin
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val etNim: TextInputEditText = findViewById(R.id.etNim)
        val etPassword: TextInputEditText = findViewById(R.id.etPassword)
        val ivPasswordVisibility: ImageView = findViewById(R.id.ivPasswordVisibility)
        val btnLogin: MaterialButton = findViewById(R.id.btnLogin)
        val tvRegister: TextView = findViewById(R.id.tvRegister)
        val tvForgotPassword: TextView = findViewById(R.id.tvForgotPassword)

        // 1. Aksi saat tombol panah kembali ditekan
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Fitur Tampilkan/Sembunyikan Password (Ikon Mata)
        ivPasswordVisibility.setOnClickListener {
            if (isPasswordVisible) {
                // Sembunyikan password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivPasswordVisibility.setImageResource(R.drawable.ic_visibility) // Menggunakan icon visibility bawaan dari XML Anda
            } else {
                // Tampilkan password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                // Jika Anda punya icon ic_visibility_off, bisa ganti kode di bawah. Jika tidak, biarkan saja.
                // ivPasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            // Pindahkan kursor teks ke paling ujung
            etPassword.setSelection(etPassword.text?.length ?: 0)
            isPasswordVisible = !isPasswordVisible
        }

        // 3. Aksi saat tombol Login ditekan
        btnLogin.setOnClickListener {
            val nim = etNim.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (nim.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Harap isi NIM dan Kata Sandi!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Memproses masuk...", Toast.LENGTH_SHORT).show()
                // Panggil fungsi pencarian NIM dan Login Firebase
                loginPakaiNIM(nim, pass)
            }
        }

        // 4. Aksi saat tulisan "Buat Akun" ditekan
        tvRegister.setOnClickListener {
            // Berpindah ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 5. Aksi saat tulisan "Lupa kata sandi?" ditekan
        tvForgotPassword.setOnClickListener {
            // Membuka LupaPasswordFragment
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, LupaPasswordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    // =======================================================
    // [TAMBAHAN FIREBASE] FUNGSI LOGIKA DATABASE
    // =======================================================
    private fun loginPakaiNIM(nim: String, pass: String) {
        // 1. Cari data di Firestore yang NIM-nya sama dengan inputan
        db.collection("users")
            .whereEqualTo("nim", nim)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "NIM tidak terdaftar!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 2. Jika NIM ketemu, ambil emailnya dari database
                val emailDitemukan = documents.documents[0].getString("email")

                if (emailDitemukan != null) {
                    // 3. Login ke Authentication menggunakan email yang ditemukan
                    auth.signInWithEmailAndPassword(emailDitemukan, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Berhasil masuk!", Toast.LENGTH_SHORT).show()

                                // Pindah ke Dashboard JIKA login berhasil
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Mencegah user kembali ke halaman login saat menekan tombol back

                            } else {
                                Toast.makeText(this, "Kata sandi salah!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Data email tidak ditemukan di sistem!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal terhubung ke database", Toast.LENGTH_SHORT).show()
            }
    }
}