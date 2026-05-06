package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    // Siapkan variabel Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Tangkap elemen dari XML
        val etNim = findViewById<EditText>(R.id.etNimRegister)
        val etPassword = findViewById<EditText>(R.id.etPasswordRegister)
        val btnRegister = findViewById<Button>(R.id.btnRegisterSubmit)

        // Logika saat tombol diklik
        btnRegister.setOnClickListener {
            val nimYangDiketik = etNim.text.toString().trim()
            val passwordYangDiketik = etPassword.text.toString().trim()

            // 1. Validasi Input Kosong
            if (nimYangDiketik.isEmpty() || passwordYangDiketik.isEmpty()) {
                Toast.makeText(this, "NIM dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Validasi Password (Firebase minimal 6 karakter)
            if (passwordYangDiketik.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Trik Konversi NIM menjadi Email
            val emailAuth = "$nimYangDiketik@student.umkt.ac.id"

            // 4. Kirim Data ke Firebase
            auth.createUserWithEmailAndPassword(emailAuth, passwordYangDiketik)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Jika sukses mendaftar
                        Toast.makeText(this, "Pendaftaran Sukses!", Toast.LENGTH_SHORT).show()

                        // Arahkan ke halaman Login
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Tutup halaman register agar tidak bisa di-back
                    } else {
                        // Jika gagal (misal NIM sudah pernah dipakai)
                        Toast.makeText(this, "Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}