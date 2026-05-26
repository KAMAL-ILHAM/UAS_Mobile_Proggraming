package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

// 1. DATA CLASS UNTUK MENAMPUNG DATA DARI API/DATABASE
data class LaporanAktif(
    val status: String,
    val judul: String,
    val lokasi: String,
    val warnaStatus: String // Hex color
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- 1. SETUP KATEGORI (MENGGUNAKAN IKON XML ASLI) ---
        setupCategory(R.id.cat1, "ELEKTRONIK", R.drawable.ic_monitor, "#EBF2FF")
        setupCategory(R.id.cat2, "FURNITUR", R.drawable.ic_furnitur, "#FFF4E5")
        setupCategory(R.id.cat3, "SANITASI", R.drawable.ic_sanitasi, "#E6F9F3")
        setupCategory(R.id.cat4, "JARINGAN", R.drawable.ic_jaringan, "#F3E8FF")
        setupCategory(R.id.cat5, "GEDUNG", R.drawable.ic_gedung, "#FFE4E6")
        setupCategory(R.id.cat6, "AREA LUAR", R.drawable.ic_outdoor, "#F0FDF4")

        // --- 2. TARIK DATA DINAMIS (USER & LAPORAN) ---
        syncUserSession()   // Tarik siapa yang sedang login
        loadDataDashboard() // Tarik apa laporan yang sedang aktif

        // --- 3. NAVIGASI KLIK ---

        // Tombol inisial (pojok kanan atas)
        findViewById<FrameLayout>(R.id.btnProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        // Tombol "Profil" di bar bawah (Bottom Navigation)
        findViewById<View>(R.id.navProfil)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        // Klik "Notifikasi" di bar bawah (Bottom Navigation)
        findViewById<View>(R.id.navNotifikasi)?.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        // Klik Card Laporan Biru (masuk ke detail)
        findViewById<CardView>(R.id.cardReport)?.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        // =========================================================
        // BAGIAN YANG DIUBAH: ALUR HALAMAN RIWAYAT
        // =========================================================

        // Klik Lihat Semua (Tampilkan Riwayat dengan Data)
        findViewById<TextView>(R.id.btnSeeAll)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            intent.putExtra("EXTRA_IS_EMPTY", false) // false = Ada datanya
            startActivity(intent)
        }

        // Klik "Riwayat" di bar bawah (Tampilkan Riwayat Kosong)
        findViewById<View>(R.id.navRiwayat)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            intent.putExtra("EXTRA_IS_EMPTY", true) // true = Kosong
            startActivity(intent)
        }

        // =========================================================

        // Klik Buat Laporan Baru
        findViewById<CardView>(R.id.cardBuatLaporan)?.setOnClickListener {
            // startActivity(Intent(this, BuatLaporanActivity::class.java))
        }
    }

    /**
     * FUNGSI A: SINKRONISASI DATA LOGIN
     * Mengecek SharedPreferences ATAU Intent untuk memastikan nama pasti muncul.
     */
    private fun syncUserSession() {
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

        // Cek SharedPreferences dulu. Kalau kosong, baru cek Intent.
        var namaLengkap = sharedPref.getString("full_name", "")
        if (namaLengkap.isNullOrEmpty()) {
            namaLengkap = intent.getStringExtra("USER_NAME") ?: "User Tidak Dikenal"
        }

        var jurusan = sharedPref.getString("major", "")
        if (jurusan.isNullOrEmpty()) {
            jurusan = intent.getStringExtra("USER_MAJOR") ?: "FARMASI"
        }

        // Logika Ekstrak Inisial Otomatis (misal: "Budi Santoso" -> "BS")
        val inisial = namaLengkap.split(" ")
            .filter { it.isNotEmpty() }
            .map { it[0] }
            .joinToString("")
            .take(2).uppercase()

        // Injeksi ke UI
        findViewById<TextView>(R.id.tvGreeting)?.text = "Halo, $namaLengkap !"
        findViewById<TextView>(R.id.tvMajor)?.text = jurusan
        findViewById<TextView>(R.id.tvInitials)?.text = inisial
    }

    /**
     * FUNGSI B: TARIK DATA DASHBOARD (LAPORAN & RIWAYAT)
     */
    private fun loadDataDashboard() {
        // ==========================================
        // SIMULASI 1: Laporan Aktif (Card Biru)
        // ==========================================
        val laporanSaatIni: LaporanAktif? = LaporanAktif(
            status = "● SEDANG DIPROSES",
            judul = "AC Lab Software",
            lokasi = "Gedung F Lt. 1, UMKT",
            warnaStatus = "#FBBC05" // Kuning Warning
        )

        val cardReport = findViewById<CardView>(R.id.cardReport)

        if (laporanSaatIni != null) {
            // Tampilkan Card Biru & Isi Teksnya
            cardReport?.visibility = View.VISIBLE

            // Menggunakan safe call (?.) agar aplikasi tidak crash jika ID belum ada di XML
            findViewById<TextView>(R.id.tvReportStatus)?.apply {
                text = laporanSaatIni.status
                setTextColor(Color.parseColor(laporanSaatIni.warnaStatus))
            }
            findViewById<TextView>(R.id.tvReportTitle)?.text = laporanSaatIni.judul
            findViewById<TextView>(R.id.tvReportLocation)?.text = laporanSaatIni.lokasi

        } else {
            // Sembunyikan Card Biru jika kosong
            cardReport?.visibility = View.GONE
        }

        // ==========================================
        // SIMULASI 2: Tampilkan/Sembunyikan List Riwayat (Bawah)
        // ==========================================
        val containerRiwayat = findViewById<View>(R.id.containerRiwayat)

        // Di-set false karena datanya akan muncul kalau sudah ada pelaporan
        val punyaRiwayat = false

        if (punyaRiwayat) {
            containerRiwayat?.visibility = View.VISIBLE
        } else {
            containerRiwayat?.visibility = View.GONE
        }
    }

    /**
     * FUNGSI C: HELPER KATEGORI
     */
    private fun setupCategory(layoutId: Int, title: String, iconRes: Int, bgColor: String) {
        val root = findViewById<View>(layoutId) ?: return

        // Set Judul
        root.findViewById<TextView>(R.id.tvCategoryName)?.text = title

        // Set Background Kotak
        root.findViewById<CardView>(R.id.cvCategoryIcon)?.setCardBackgroundColor(Color.parseColor(bgColor))

        // Set Ikon Asli XML
        val iconImg = root.findViewById<ImageView>(R.id.ivCategoryIcon)
        iconImg?.setImageResource(iconRes)
        iconImg?.clearColorFilter()
    }
}