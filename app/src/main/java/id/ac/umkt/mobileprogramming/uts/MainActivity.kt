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
                startActivity(Intent(this, DetailRiwayatActivity::class.java))
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
                startActivity(Intent(this, BuatLaporanActivity::class.java))
            }
        }

        /**
         * FUNGSI A: SINKRONISASI DATA LOGIN & TITIK MERAH
         */
        private fun syncUserSession() {
            val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
            var namaLengkap = sharedPref.getString("full_name", "")
            if (namaLengkap.isNullOrEmpty()) {
                namaLengkap = intent.getStringExtra("USER_NAME") ?: "User Tidak Dikenal"
            }

            // --- LOGIKA TITIK MERAH BARU ---
            // Kita gunakan ?: "" agar pasti terbaca sebagai String dan tidak error
            val major = sharedPref.getString("major", "") ?: ""
            val phone = sharedPref.getString("phone", "") ?: ""
            val badgeProfil = findViewById<View>(R.id.badgeProfilRedDot)

            // Jika salah satu masih kosong, munculkan titik merah
            if (major.isEmpty() || phone.isEmpty()) {
                badgeProfil?.visibility = View.VISIBLE
            } else {
                badgeProfil?.visibility = View.GONE
            }

            // --- LOGIKA INISIAL & SUNTIK UI (YANG SEMPAT TERHAPUS) ---
            val inisial = namaLengkap.split(" ")
                .filter { it.isNotEmpty() }
                .map { it[0] }
                .joinToString("")
                .take(2).uppercase()

            findViewById<TextView>(R.id.tvGreeting)?.text = "Halo, $namaLengkap !"
            // Jika prodi belum diisi, tampilkan peringatan di Beranda
            findViewById<TextView>(R.id.tvMajor)?.text = if (major.isEmpty()) "PRODI BELUM DIISI" else major
            findViewById<TextView>(R.id.tvInitials)?.text = inisial
        }
        /**
         * FUNGSI B: TARIK DATA DASHBOARD (LAPORAN & RIWAYAT)
         */
        private fun loadDataDashboard() {
            val sharedPref = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
            val savedKategori = sharedPref.getString("kategori", null)

            val cardReport = findViewById<CardView>(R.id.cardReport)
            val containerRiwayat = findViewById<View>(R.id.containerRiwayat)

            // Jika savedKategori tidak null, berarti ada laporan yang aktif!
            if (savedKategori != null) {
                val savedLokasi = sharedPref.getString("lokasi", "Gedung UMKT")
                val savedStatus = sharedPref.getString("status", "● SEDANG DIPROSES")

                // Tampilkan Card Biru & Isi Teksnya dari memori lokal
                cardReport?.visibility = View.VISIBLE
                containerRiwayat?.visibility = View.VISIBLE

                findViewById<TextView>(R.id.tvReportStatus)?.apply {
                    text = savedStatus
                    setTextColor(Color.parseColor("#FBBC05")) // Kuning Warning
                }
                findViewById<TextView>(R.id.tvReportTitle)?.text = savedKategori
                findViewById<TextView>(R.id.tvReportLocation)?.text = savedLokasi
            } else {
                // Sembunyikan Card Biru dan Riwayat jika belum pernah melapor (kosong)
                cardReport?.visibility = View.GONE
                containerRiwayat?.visibility = View.GONE
            }
        }

        /**
         * FUNGSI C: HELPER KATEGORI
         */
        private fun setupCategory(layoutId: Int, title: String, iconRes: Int, bgColor: String) {
            // Baris ini sangat penting agar 'root' tidak merah
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

        override fun onResume() {
            super.onResume()
            // Paksa aplikasi mengecek ulang session setiap kali Beranda terbuka lagi
            syncUserSession()
        }
    }