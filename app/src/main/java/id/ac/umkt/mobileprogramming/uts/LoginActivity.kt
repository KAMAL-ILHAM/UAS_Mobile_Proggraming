package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    // DEKLARASI FIREBASE
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INISIALISASI FIREBASE AUTHENTICATION
        auth = FirebaseAuth.getInstance()

        // 2. CEK AUTO-LOGIN: Jika user punya sesi, langsung lempar ke MainActivity
        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return // Hentikan eksekusi, jangan muat halaman login sama sekali
        }

        // 3. JIKA BELUM LOGIN: Baru muat UI halaman login
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        // 4. Kenalkan Views
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnMasuk = findViewById<MaterialButton>(R.id.btnMasuk)
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)
        val inputLayoutNim = findViewById<TextInputLayout>(R.id.inputLayoutNim)
        val etNim = findViewById<TextInputEditText>(R.id.etNim)
        val inputLayoutPassword = findViewById<TextInputLayout>(R.id.inputLayoutPassword)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        // 5. Animasi Masuk Halaman
        layoutMainContent.translationY = 80f
        layoutMainContent.alpha = 0f
        layoutMainContent.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        btnBack.setOnClickListener { finish() }

        // --- TANGKAP DEEP LINK DARI EMAIL FIREBASE ---
        val uri = intent.data
        if (uri != null && uri.getQueryParameter("mode") == "resetPassword") {
            val oobCode = uri.getQueryParameter("oobCode")
            if (oobCode != null) {
                // Bungkus oobCode dan kirim ke Fragment
                val bundle = Bundle()
                bundle.putString("OOB_CODE", oobCode)

                val resetFragment = BuatSandiBaruFragment()
                resetFragment.arguments = bundle

                // Langsung buka halaman ganti sandi
                supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, resetFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 6. Navigasi ke Register
        findViewById<LinearLayout>(R.id.layoutRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 7. Lupa Password
        findViewById<TextView>(R.id.tvLupaSandi).setOnClickListener {
            val lupaPasswordFragment = LupaPasswordFragment()
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, lupaPasswordFragment)
                .addToBackStack(null)
                .commit()
        }

        // 8. Tombol Masuk (SUDAH TERHUBUNG FIREBASE)
        btnMasuk.setOnClickListener {
            inputLayoutNim.error = null
            inputLayoutPassword.error = null

            val nim = etNim.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nim.isEmpty()) inputLayoutNim.error = "Field tidak boleh kosong"
            if (password.isEmpty()) inputLayoutPassword.error = "Field tidak boleh kosong"

            if (nim.isEmpty() || password.isEmpty()) {
                return@setOnClickListener
            }

            // State Loading
            btnMasuk.isEnabled = false
            btnMasuk.text = "Memvalidasi..."
            btnMasuk.icon = null

            // LOGIKA FIREBASE AUTHENTICATION
            val emailLogin = "$nim@umkt.ac.id" // Format email kampus otomatis

            auth.signInWithEmailAndPassword(emailLogin, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Jika berhasil login, tarik data nama pengguna dari Firestore
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val namaLengkap = document.getString("nama") ?: "User UMKT"

                                    // --- TARIK DATA BARU DARI FIREBASE ---
                                    val major = document.getString("major") ?: ""
                                    val phone = document.getString("phone") ?: ""

                                    val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
                                    with(sharedPref.edit()) {
                                        putString("full_name", namaLengkap)
                                        putString("nim", nim)
                                        // --- SIMPAN KE LOKAL ---
                                        putString("major", major)
                                        putString("phone", phone)
                                        apply()
                                    }

                                    Toast.makeText(this, "Selamat datang, $namaLengkap", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    // Gagal tarik nama, tetap lanjutkan ke dashboard
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    } else {
                        // Jika Gagal Login (Sandi salah / Belum daftar)
                        btnMasuk.isEnabled = true
                        btnMasuk.text = "Masuk Sistem"
                        btnMasuk.setIconResource(R.drawable.ic_login_arrow)
                        inputLayoutPassword.error = "NIM atau Kata Sandi salah"
                        Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}