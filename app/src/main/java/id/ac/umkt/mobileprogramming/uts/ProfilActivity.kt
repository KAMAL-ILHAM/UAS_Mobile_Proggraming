package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        // 1. Sinkronkan Data dari Database (Session)
        loadUserProfile()

        // 2. Setup Tampilan Menu (Icon & Label)
        setupMenu()

        // --- TAMBAHAN BARU: LOGIKA KLIK MENU ---

        // Klik menu Informasi Pribadi
        findViewById<View>(R.id.menuInfo)?.setOnClickListener {
            val intent = Intent(this, InformasiPribadiActivity::class.java)
            startActivity(intent)
        }

        // Klik menu Pusat Bantuan (FAQ)
        findViewById<View>(R.id.menuFaq)?.setOnClickListener {
            val intent = Intent(this, PusatBantuanActivity::class.java)
            startActivity(intent)
        }

        // ----------------------------------------

        // 3. Logika Keluar Akun
        findViewById<View>(R.id.btnLogout)?.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val name = sharedPref.getString("full_name", "User UMKT") ?: "User UMKT"
        val major = sharedPref.getString("major", "TEKNIK INFORMATIKA") ?: "TEKNIK INFORMATIKA"

        // NIM biasanya kita simpan juga saat login, misal key-nya "nim"
        val nim = sharedPref.getString("nim", "2411102441015") ?: "2411102441015"

        findViewById<TextView>(R.id.tvProfileName)?.text = name
        findViewById<TextView>(R.id.tvProfileEmail)?.text = "$nim@umkt.ac.id"
        findViewById<TextView>(R.id.tvProfileMajor)?.text = "$major, UMKT"

        // Inisial
        val initials = name.split(" ").filter { it.isNotEmpty() }.map { it[0] }.joinToString("").take(2).uppercase()
        findViewById<TextView>(R.id.tvProfileInitials)?.text = initials
    }

    private fun setupMenu() {
        // 1. Menu Informasi Pribadi (Background Biru Muda, Ikon Biru)
        setupMenuItem(
            layoutId = R.id.menuInfo,
            title = "Informasi Pribadi",
            iconRes = R.drawable.ic_profile_user,
            bgColorHex = "#EBF2FF",   // Soft Blue
            iconTintHex = "#2563EB"  // Dark Blue
        )

        // 2. Menu Keamanan & Sandi (Background Oranye Muda, Ikon Oranye)
        setupMenuItem(
            layoutId = R.id.menuSandi,
            title = "Keamanan & Sandi",
            iconRes = R.drawable.ic_shield_lock,
            bgColorHex = "#FFF4E5",  // Soft Orange
            iconTintHex = "#FF9800" // Dark Orange
        )

        // 3. Menu FAQ (Background Hijau Muda, Ikon Hijau)
        setupMenuItem(
            layoutId = R.id.menuFaq,
            title = "Pusat Bantuan (FAQ)",
            iconRes = R.drawable.ic_help_circle,
            bgColorHex = "#E6F9F3",  // Soft Green
            iconTintHex = "#10B981" // Dark Green
        )

        // 4. Menu Tentang Aplikasi (Background Ungu Muda, Ikon Ungu)
        setupMenuItem(
            layoutId = R.id.menuTentang,
            title = "Tentang Aplikasi",
            iconRes = R.drawable.ic_info_circle,
            bgColorHex = "#F3E8FF",  // Soft Purple
            iconTintHex = "#9333EA" // Dark Purple
        )

        // Setup v1.2.0 (Extra text) tetap statis
        findViewById<View>(R.id.menuTentang)?.findViewById<TextView>(R.id.tvMenuExtra)?.apply {
            visibility = View.VISIBLE
            text = "v1.2.0"
        }
    }

    /**
     * HELPER FUNCTION: Menghindari copy-paste kode yang sama berulang kali.
     * Mengatur ID, Warna BG kotak, Gambar Ikon, dan Warna Ikon (Tint).
     */
    private fun setupMenuItem(layoutId: Int, title: String, iconRes: Int, bgColorHex: String, iconTintHex: String) {
        val root = findViewById<View>(layoutId) ?: return

        // 1. Set Teks Judul
        root.findViewById<TextView>(R.id.tvMenuTitle).text = title

        // 2. Set Background Kotak Ikon (Warna-warni sesuai input)
        val iconCard = root.findViewById<CardView>(R.id.cvMenuIconBg)
        iconCard.setCardBackgroundColor(Color.parseColor(bgColorHex))

        // 3. Set Ikon
        val iconImg = root.findViewById<ImageView>(R.id.ivMenuIcon)
        iconImg.setImageResource(iconRes)

        // 4. Set Warna Ikon (Tint) agar tidak hitam kaku
        iconImg.setColorFilter(Color.parseColor(iconTintHex))
    }

    private fun logoutUser() {
        // Hapus Session
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Balik ke Login
        Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showLogoutDialog() {
        // Membuat Custom Dialog
        val dialog = android.app.Dialog(this)
        dialog.setContentView(R.layout.dialog_logout)

        // Membuat background bawaan menjadi transparan agar sudut melengkung CardView terlihat
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Mengatur lebar dialog
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true) // Bisa ditutup dengan klik area luar

        // Inisialisasi Tombol di dalam Dialog
        val btnYes = dialog.findViewById<View>(R.id.btnYesLogout)
        val btnCancel = dialog.findViewById<View>(R.id.btnCancelLogout)

        btnYes.setOnClickListener {
            dialog.dismiss() // Tutup dialog
            logoutUser()     // Jalankan fungsi logout asli
        }

        btnCancel.setOnClickListener {
            dialog.dismiss() // Batal dan tutup dialog
        }

        // Tampilkan Dialog
        dialog.show()
    }
}