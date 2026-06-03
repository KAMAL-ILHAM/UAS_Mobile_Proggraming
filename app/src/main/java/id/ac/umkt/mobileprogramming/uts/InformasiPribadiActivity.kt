package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InformasiPribadiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi_pribadi)

        // 1. Kenalkan Views
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnUbahFoto = findViewById<TextView>(R.id.btnUbahFoto)
        val btnSimpan = findViewById<MaterialButton>(R.id.btnSimpan)
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)
        val avatarContainer = findViewById<FrameLayout>(R.id.avatarContainer)

        val tvNamaLengkap = findViewById<TextView>(R.id.tvNamaLengkap)
        val tvNim = findViewById<TextView>(R.id.tvNim)
        val tvInisialAvatar = findViewById<TextView>(R.id.tvInisialAvatar)

        // View khusus untuk Program Studi (Berupa tombol yang memanggil Bottom Sheet)
        val layoutPilihProdi = findViewById<LinearLayout>(R.id.layoutPilihProdi)
        val tvProgramStudi = findViewById<TextView>(R.id.tvProgramStudi)
        val etWhatsapp = findViewById<EditText>(R.id.etWhatsapp)

        // 2. Tarik Data Sesi dari Memori Lokal
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val sessionNama = sharedPref.getString("full_name", "User UMKT") ?: "User UMKT"
        val sessionNim = sharedPref.getString("nim", "Kosong")
        val sessionProdi = sharedPref.getString("major", "")
        val sessionPhone = sharedPref.getString("phone", "")

        val initials = sessionNama.split(" ").filter { it.isNotEmpty() }.map { it[0] }.joinToString("").take(2).uppercase()

        // 3. Suntikkan Data ke UI
        tvNamaLengkap.text = sessionNama
        tvNim.text = sessionNim
        tvInisialAvatar.text = initials
        tvProgramStudi.text = sessionProdi
        etWhatsapp.setText(sessionPhone)

        // 4. Animasi Masuk
        setupEntranceAnimations(layoutMainContent, avatarContainer)

        // 5. Logika Klik
        btnBack.setOnClickListener { finish() }

        btnUbahFoto.setOnClickListener {
            Toast.makeText(this, "Membuka galeri...", Toast.LENGTH_SHORT).show()
        }

        // 🔥 LOGIKA BARU: Buka Bottom Sheet Pencarian Saat Kolom Prodi Diklik
        layoutPilihProdi.setOnClickListener {
            showProdiBottomSheet(tvProgramStudi)
        }

        btnSimpan.setOnClickListener {
            val inputProdi = tvProgramStudi.text.toString().trim()
            val inputPhone = etWhatsapp.text.toString().trim()

            if (inputProdi.isEmpty() || inputProdi == "Pilih Program Studi" || inputPhone.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi dengan benar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSimpan.isEnabled = false
            btnSimpan.text = "Menyimpan..."
            btnSimpan.icon = null

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()

                db.collection("users").document(userId).update(
                    mapOf(
                        "major" to inputProdi,
                        "phone" to inputPhone
                    )
                ).addOnSuccessListener {
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

    // 🔥 FUNGSI BARU: Bottom Sheet Dialog dengan Fitur Searching
    private fun showProdiBottomSheet(tvTarget: TextView) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_prodi, null)

        val etSearchProdi = view.findViewById<EditText>(R.id.etSearchProdi)
        val lvProdi = view.findViewById<ListView>(R.id.lvProdi)

        val daftarProdi = arrayOf(
            "Teknik Informatika", "Teknik Geologi", "Teknik Mesin", "Teknik Sipil",
            "Kesehatan Masyarakat","Kesehatan Lingkungan","Agribisnis","Profesi Ners","Agroteknologi","Hubungan Internasional", "Keperawatan","Kedokteran","Bisnis Digital", "Farmasi", "Manajemen",
            "Akuntansi", "Psikologi", "Hukum", "Pendidikan Olahraga", "Pendidikan Bahasa Inggris"
        )

        // Pasangkan data ke dalam List
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, daftarProdi)
        lvProdi.adapter = adapter

        // Logika Menyortir / Searching secara Real-time
        etSearchProdi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Logika saat jurusan dipilih
        lvProdi.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            tvTarget.text = selectedItem
            tvTarget.setTextColor(android.graphics.Color.parseColor("#0F172A")) // Ubah warna jadi gelap
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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