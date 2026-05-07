package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.text.InputType
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // Deklarasi variabel Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Variabel untuk mengecek status ikon mata (password)
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Hubungkan elemen UI dengan ID yang ada di XML
        val btnBack: ImageView = findViewById(R.id.btnBackRegister)
        val btnDaftar: MaterialButton = findViewById(R.id.btnDaftar)
        val tvMasukDisini: TextView = findViewById(R.id.tvMasukDisini)
        val tvTerms: TextView = findViewById(R.id.tvTerms)

        // Menggunakan TextInputEditText sesuai dengan XML Anda
        val etNim: TextInputEditText = findViewById(R.id.etNimRegister)
        val etNama: TextInputEditText = findViewById(R.id.etNamaRegister)
        val etEmail: TextInputEditText = findViewById(R.id.etEmailRegister)
        val etPassword: TextInputEditText = findViewById(R.id.etPasswordRegister)
        val ivPasswordVisibility: ImageView = findViewById(R.id.ivPasswordVisibilityReg)

        // Terjemahkan string HTML-nya ke dalam TextView
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

        // Fitur Tampilkan/Sembunyikan Password (Ikon Mata)
        ivPasswordVisibility.setOnClickListener {
            if (isPasswordVisible) {
                // Sembunyikan password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivPasswordVisibility.setImageResource(R.drawable.ic_visibility_off) // Sesuai dengan XML Anda
            } else {
                // Tampilkan password (CATATAN: Pastikan Anda punya icon ic_visibility di folder drawable)
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                // Jika tidak punya ic_visibility, biarkan saja baris di bawah ini atau ganti icon lain
                // ivPasswordVisibility.setImageResource(R.drawable.ic_visibility)
            }
            // Pindahkan kursor teks ke paling ujung
            etPassword.setSelection(etPassword.text?.length ?: 0)
            isPasswordVisible = !isPasswordVisible
        }

        // Tombol Daftar Sekarang
        btnDaftar.setOnClickListener {
            // Ambil data yang diketik user
            val nim = etNim.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // Validasi input
            if (nim.isEmpty() || nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom pendaftaran!", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 8) {
                // Sesuai dengan hint di XML (Minimal 8 karakter)
                Toast.makeText(this, "Password minimal 8 karakter!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Memproses pendaftaran...", Toast.LENGTH_SHORT).show()
                // Panggil fungsi register ke Firebase
                registerKampus(nim, nama, email, pass)
            }
        }
    }

    // =======================================================
    // FUNGSI LOGIKA DATABASE (FIREBASE)
    // =======================================================
    private fun registerKampus(nim: String, nama: String, email: String, pass: String) {
        // 1. Buat akun di Authentication
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    // 2. Siapkan data untuk Firestore
                    val dataMahasiswa = hashMapOf(
                        "nim" to nim,
                        "nama_lengkap" to nama,
                        "email" to email
                    )

                    // 3. Simpan ke koleksi "users"
                    if (uid != null) {
                        db.collection("users").document(uid)
                            .set(dataMahasiswa)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                                // Tutup halaman Register dan kembali ke halaman Login
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal simpan profil: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Gagal Register: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}