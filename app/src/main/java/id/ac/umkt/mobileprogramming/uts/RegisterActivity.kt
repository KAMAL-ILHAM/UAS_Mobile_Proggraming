package id.ac.umkt.mobileprogramming.uts // Pastikan package ini sesuai dengan milikmu

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // 1. Deklarasikan variabel global agar bisa diakses di seluruh class
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Pastikan nama layout benar
        // --- RENDER HTML SYARAT & KETENTUAN (PIXEL PERFECT) ---
        val tvTerms = findViewById<TextView>(R.id.tvTerms)

        // Tulis format HTML secara langsung di sini agar tidak bentrok dengan aturan XML
        // <u> digunakan untuk garis bawah (underline)
        // <b> digunakan untuk tebal (bold)
        val htmlText = "Dengan mendaftar, Anda menyetujui <font color='#2563EB'><b><u>Syarat &amp; Ketentuan</u></b></font> serta<br>kebijakan privasi kampus."

        // Render string di atas menjadi visual yang nyata
        tvTerms.text = androidx.core.text.HtmlCompat.fromHtml(
            htmlText,
            androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        // ------------------------------------------------------
        // 2. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 3. Hubungkan variabel dengan ID yang ada di XML (TETAP PAKAI NAMA LAMA)
        val etNim = findViewById<TextInputEditText>(R.id.etNimRegister)
        val etNama = findViewById<TextInputEditText>(R.id.etNamaRegister)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailRegister)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordRegister)
        val btnDaftar = findViewById<MaterialButton>(R.id.btnDaftar)
        val btnBack = findViewById<ImageView>(R.id.btnBackRegister)

        btnBack.setOnClickListener { finish() }

        // 4. Logika Pendaftaran
        btnDaftar.setOnClickListener {
            val nim = etNim.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nim.isEmpty() || nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Harap lengkapi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this@RegisterActivity, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ubah teks tombol saat loading agar tidak diklik dua kali
            btnDaftar.isEnabled = false
            btnDaftar.text = "Memproses..."

            // Proses Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegisterActivity) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        val userData = hashMapOf(
                            "nim" to nim,
                            "nama" to nama, // Ini akan menyimpan nama yang diketik user
                            "email" to email,
                            "timestamp" to System.currentTimeMillis()
                        )

                        if (userId != null) {
                            db.collection("users").document(userId)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this@RegisterActivity, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()

                                    // --- INI TAMBAHAN NAVIGASI KE HALAMAN SUKSES ---
                                    val intent = Intent(this@RegisterActivity, RegisterSuccessActivity::class.java)
                                    intent.putExtra("USER_NAME", nama) // Mengirim nama secara dinamis dari form
                                    startActivity(intent)
                                    finish() // Tutup halaman form register
                                    // ---------------------------------------------
                                }
                                .addOnFailureListener { e ->
                                    btnDaftar.isEnabled = true
                                    btnDaftar.text = "Daftar Sekarang"
                                    android.util.Log.e("FirebaseError", "Firestore Gagal: ${e.message}")
                                }
                        }
                    } else {
                        btnDaftar.isEnabled = true
                        btnDaftar.text = "Daftar Sekarang"
                        android.util.Log.e("FirebaseError", "Auth Gagal: ${task.exception?.message}")
                        Toast.makeText(this@RegisterActivity, "Gagal Daftar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}