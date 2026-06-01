package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class InformasiPribadiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi_pribadi)

        // 1. Kenalkan Views (Semua dipanggil pakai ID agar 100% Anti-Force Close)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnUbahFoto = findViewById<TextView>(R.id.btnUbahFoto)
        val btnSimpan = findViewById<MaterialButton>(R.id.btnSimpan)
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)
        val avatarContainer = findViewById<FrameLayout>(R.id.avatarContainer)

        val tvNamaLengkap = findViewById<TextView>(R.id.tvNamaLengkap)
        val tvNim = findViewById<TextView>(R.id.tvNim)
        val tvInisialAvatar = findViewById<TextView>(R.id.tvInisialAvatar)
        val etProgramStudi = findViewById<EditText>(R.id.etProgramStudi)
        val etWhatsapp = findViewById<EditText>(R.id.etWhatsapp)

        // 2. Tarik Data Sesi dari Memori Lokal
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val sessionNama = sharedPref.getString("full_name", "User UMKT") ?: "User UMKT"
        val sessionNim = sharedPref.getString("nim", "Kosong")
        val sessionProdi = sharedPref.getString("major", "")
        val sessionPhone = sharedPref.getString("phone", "")

        // Ekstrak Inisial
        val initials = sessionNama.split(" ").filter { it.isNotEmpty() }.map { it[0] }.joinToString("").take(2).uppercase()

        // 3. Suntikkan Data ke UI
        tvNamaLengkap.text = sessionNama
        tvNim.text = sessionNim
        tvInisialAvatar.text = initials
        etProgramStudi.setText(sessionProdi)
        etWhatsapp.setText(sessionPhone)

        // 4. Animasi Masuk
        setupEntranceAnimations(layoutMainContent, avatarContainer)

        // 5. Logika Klik
        btnBack.setOnClickListener { finish() }

        btnUbahFoto.setOnClickListener {
            Toast.makeText(this, "Membuka galeri...", Toast.LENGTH_SHORT).show()
        }

        btnSimpan.setOnClickListener {
            val inputProdi = etProgramStudi.text.toString().trim()
            val inputPhone = etWhatsapp.text.toString().trim()

            if (inputProdi.isEmpty() || inputPhone.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSimpan.isEnabled = false
            btnSimpan.text = "Menyimpan..."
            btnSimpan.icon = null

            // SIMPAN KE FIREBASE SEKALIGUS KE MEMORI LOKAL
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                // Update dokumen user di database
                db.collection("users").document(userId).update(
                    mapOf(
                        "major" to inputProdi,
                        "phone" to inputPhone
                    )
                ).addOnSuccessListener {
                    // Jika sukses tersimpan di Firebase, simpan di memori lokal
                    with(sharedPref.edit()) {
                        putString("major", inputProdi)
                        putString("phone", inputPhone)
                        apply()
                    }

                    btnSimpan.text = "Tersimpan!"
                    btnSimpan.setBackgroundColor(android.graphics.Color.parseColor("#10B981"))
                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({ finish() }, 1000)
                }.addOnFailureListener {
                    btnSimpan.isEnabled = true
                    btnSimpan.text = "Simpan Perubahan"
                    Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupEntranceAnimations(contentView: LinearLayout, avatarView: FrameLayout) {
        contentView.translationY = 80f
        contentView.alpha = 0f
        contentView.animate().translationY(0f).alpha(1f).setDuration(500).setInterpolator(DecelerateInterpolator()).start()

        avatarView.scaleX = 0f
        avatarView.scaleY = 0f
        avatarView.animate().scaleX(1f).scaleY(1f).setDuration(600).setStartDelay(100).setInterpolator(OvershootInterpolator(1.2f)).start()
    }
}