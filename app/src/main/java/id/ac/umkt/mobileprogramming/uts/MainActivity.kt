package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var namaUserAktif: String = ""

    // Data untuk Riwayat Bawah
    private var allMyReports = listOf<DocumentSnapshot>()
    private var currentFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        // --- 1. SETUP KATEGORI ---
        setupCategory(R.id.cat1, "Elektronik", R.drawable.ic_monitor, "#EBF2FF")
        setupCategory(R.id.cat2, "Furnitur", R.drawable.ic_furnitur, "#FFF4E5")
        setupCategory(R.id.cat3, "Sanitasi", R.drawable.ic_sanitasi, "#E6F9F3")
        setupCategory(R.id.cat4, "Jaringan", R.drawable.ic_jaringan, "#F3E8FF")
        setupCategory(R.id.cat5, "Gedung", R.drawable.ic_gedung, "#FFE4E6")
        setupCategory(R.id.cat6, "Area Luar", R.drawable.ic_outdoor, "#F0FDF4")

        // --- 2. TARIK DATA AWAL ---
        syncUserSession()
        loadDataDashboard()

        // --- 3. NAVIGASI KLIK BAWAAN ---
        findViewById<CardView>(R.id.cardBuatLaporan)?.setOnClickListener {
            startActivity(Intent(this, BuatLaporanActivity::class.java))
        }
        findViewById<FrameLayout>(R.id.btnProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }
        findViewById<View>(R.id.navProfil)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }
        findViewById<View>(R.id.navNotifikasi)?.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
        // Card Biru
        findViewById<CardView>(R.id.cardReport)?.setOnClickListener {
            startActivity(Intent(this, DetailRiwayatActivity::class.java))
        }

        // --- 4. TOMBOL LIHAT SEMUA ---
        findViewById<TextView>(R.id.btnSeeAll)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            intent.putExtra("EXTRA_IS_EMPTY", allMyReports.isEmpty())
            startActivity(intent)
        }
        findViewById<View>(R.id.navRiwayat)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            intent.putExtra("EXTRA_IS_EMPTY", allMyReports.isEmpty())
            startActivity(intent)
        }
    }

    private fun syncUserSession() {
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        var namaLengkap = sharedPref.getString("full_name", "")
        if (namaLengkap.isNullOrEmpty()) {
            namaLengkap = intent.getStringExtra("USER_NAME") ?: "User Tidak Dikenal"
        }

        namaUserAktif = namaLengkap.trim()
        val major = sharedPref.getString("major", "") ?: ""
        val phone = sharedPref.getString("phone", "") ?: ""

        val badgeProfil = findViewById<View>(R.id.badgeProfilRedDot)
        if (major.isEmpty() || phone.isEmpty()) {
            badgeProfil?.visibility = View.VISIBLE
        } else {
            badgeProfil?.visibility = View.GONE
        }

        val inisial = namaLengkap.split(" ").filter { it.isNotEmpty() }.map { it[0] }.joinToString("").take(2).uppercase()

        findViewById<TextView>(R.id.tvGreeting)?.text = "Halo, $namaLengkap !"
        findViewById<TextView>(R.id.tvMajor)?.text = if (major.isEmpty()) "PRODI BELUM DIISI" else major
        findViewById<TextView>(R.id.tvInitials)?.text = inisial
    }

    // --- LOGIKA FILTER KATEGORI ---
    private fun handleCategoryClick(clickedFilterVal: String) {
        currentFilter = if (currentFilter.equals(clickedFilterVal, ignoreCase = true)) null else clickedFilterVal

        val categoryConfigs = listOf(
            Pair(R.id.cat1, "Elektronik"), Pair(R.id.cat2, "Furnitur"), Pair(R.id.cat3, "Sanitasi"),
            Pair(R.id.cat4, "Jaringan"), Pair(R.id.cat5, "Gedung"), Pair(R.id.cat6, "Area Luar")
        )

        for ((id, filterVal) in categoryConfigs) {
            val view = findViewById<View>(id)
            if (currentFilter == null) {
                view?.alpha = 1.0f
            } else {
                view?.alpha = if (filterVal.equals(currentFilter, ignoreCase = true)) 1.0f else 0.4f
            }
        }
        refreshBottomRiwayat()
    }

    private fun loadDataDashboard() {
        val cardReport = findViewById<CardView>(R.id.cardReport)


        val sharedPrefLokal = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
        val localKategori = sharedPrefLokal.getString("kategori", null)
        val localLokasi = sharedPrefLokal.getString("lokasi", "Gedung UMKT")
        val localStatus = sharedPrefLokal.getString("status", "● SEDANG DIPROSES")

        if (localKategori != null) {
            cardReport?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvReportStatus)?.apply {
                text = localStatus
                setTextColor(Color.parseColor("#FBBC05"))
            }
            findViewById<TextView>(R.id.tvReportTitle)?.text = localKategori
            findViewById<TextView>(R.id.tvReportLocation)?.text = localLokasi
        } else {
            cardReport?.visibility = View.GONE
        }

        if (namaUserAktif.isEmpty() || namaUserAktif == "User Tidak Dikenal") {
            return
        }

        db.collection("laporan")
            .get()
            .addOnSuccessListener { documents ->
                val laporanSaya = documents.filter { doc ->
                    val pelapor = doc.getString("nama_pelapor")?.trim() ?: ""
                    pelapor.equals(namaUserAktif, ignoreCase = true)
                }

                // Simpan data mentah untuk filter Riwayat Bawah
                allMyReports = laporanSaya

                if (laporanSaya.isNotEmpty()) {
                    val dokumenTerbaru = laporanSaya.maxByOrNull {
                        it.getTimestamp("timestamp")?.toDate()?.time ?: 0L
                    }

                    if (dokumenTerbaru != null) {
                        val dbKategori = dokumenTerbaru.getString("kategori") ?: "Kategori Tidak Diketahui"
                        val dbLokasi = dokumenTerbaru.getString("lokasi") ?: "Lokasi Tidak Diketahui"
                        val dbStatus = dokumenTerbaru.getString("status") ?: "● SEDANG DIPROSES"

                        cardReport?.visibility = View.VISIBLE

                        findViewById<TextView>(R.id.tvReportStatus)?.apply {
                            text = dbStatus
                            if (dbStatus.contains("SELESAI", ignoreCase = true)) {
                                setTextColor(Color.parseColor("#2ECC71"))
                            } else {
                                setTextColor(Color.parseColor("#FBBC05"))
                            }
                        }
                        findViewById<TextView>(R.id.tvReportTitle)?.text = dbKategori
                        findViewById<TextView>(R.id.tvReportLocation)?.text = dbLokasi

                        with (sharedPrefLokal.edit()) {
                            putString("kategori", dbKategori)
                            putString("lokasi", dbLokasi)
                            putString("status", dbStatus)
                            apply()
                        }
                    }
                } else {
                    cardReport?.visibility = View.GONE
                    sharedPrefLokal.edit().clear().apply()
                }

                // Trigger render Riwayat Bawah
                refreshBottomRiwayat()
            }
            .addOnFailureListener {
                if (localKategori == null) {
                    cardReport?.visibility = View.GONE
                }
            }
    }

    // --- FUNGSI KHUSUS UNTUK LIST RIWAYAT BAWAH ---
    private fun refreshBottomRiwayat() {
        val containerRiwayat = findViewById<LinearLayout>(R.id.containerRiwayat)
        containerRiwayat?.removeAllViews()

        val filteredReports = if (currentFilter != null) {
            allMyReports.filter { doc ->
                val kat = doc.getString("kategori") ?: ""
                kat.equals(currentFilter, ignoreCase = true)
            }
        } else {
            allMyReports
        }

        // 1. Urutkan SEMUA data dari yang paling baru ke paling lama
        val sortedReports = filteredReports.sortedByDescending {
            it.getTimestamp("timestamp")?.toDate()?.time ?: 0L
        }.take(3)

        // 2. Lakukan perulangan (Looping) untuk menampilkan SEMUA laporan
        for (dokumen in sortedReports) {
            val inflater = LayoutInflater.from(this)
            val viewRiwayat = inflater.inflate(R.layout.item_riwayat, containerRiwayat, false)

            val currentDocId = dokumen.id
            val currentDocTiket = dokumen.getString("nomor_tiket") ?: ""

            val dbKategori = dokumen.getString("kategori") ?: "Fasilitas"
            val dbLokasi = dokumen.getString("lokasi") ?: "Kampus UMKT"
            val dbStatus = dokumen.getString("status") ?: "SEDANG DIPROSES"
            val dbDetail = dokumen.getString("detail") ?: dbKategori

            val date = dokumen.getTimestamp("timestamp")?.toDate()
            val timeAgoString = getTimeAgo(date)

            val shortDetail = if (dbDetail.length > 28) "${dbDetail.take(28)}..." else dbDetail

            val tvJudul = viewRiwayat.findViewById<TextView>(R.id.tvRiwayatTitle)
            val tvSub = viewRiwayat.findViewById<TextView>(R.id.tvRiwayatDesc)
            val tvStatus = viewRiwayat.findViewById<TextView>(R.id.tvRiwayatStatus)
            val ivIcon = viewRiwayat.findViewById<ImageView>(R.id.ivRiwayatIcon)
            val llStatus = viewRiwayat.findViewById<LinearLayout>(R.id.llRiwayatStatus)

            tvJudul?.text = shortDetail
            tvSub?.text = "$timeAgoString • $dbLokasi"
            tvStatus?.text = dbStatus.replace("● ", "")

            val bgDrawable = android.graphics.drawable.GradientDrawable().apply { cornerRadius = 20f }

            when {
                dbStatus.contains("SELESAI", ignoreCase = true) -> {
                    tvStatus?.setTextColor(Color.parseColor("#10B981"))
                    bgDrawable.setColor(Color.parseColor("#E6F9F3"))
                }
                dbStatus.contains("BATAL", ignoreCase = true) -> {
                    tvStatus?.setTextColor(Color.parseColor("#EF4444"))
                    bgDrawable.setColor(Color.parseColor("#FEE2E2"))
                }
                else -> {
                    tvStatus?.setTextColor(Color.parseColor("#FBBC05"))
                    bgDrawable.setColor(Color.parseColor("#FEF5E6"))
                }
            }
            llStatus?.background = bgDrawable

            val iconRes = when (dbKategori.lowercase()) {
                "elektronik" -> R.drawable.ic_monitor
                "furnitur" -> R.drawable.ic_furnitur
                "sanitasi" -> R.drawable.ic_sanitasi
                "jaringan" -> R.drawable.ic_jaringan
                "gedung" -> R.drawable.ic_gedung
                "area luar" -> R.drawable.ic_outdoor
                else -> R.drawable.ic_document
            }
            ivIcon?.setImageResource(iconRes)

            viewRiwayat.setOnClickListener {
                val intent = Intent(this, DetailRiwayatActivity::class.java)
                intent.putExtra("DOCUMENT_ID", currentDocId)
                intent.putExtra("NOMOR_TIKET", currentDocTiket)
                startActivity(intent)
            }

            // Tambahkan SATU PER SATU ke dalam layar sampai semua data habis
            containerRiwayat?.addView(viewRiwayat)
        }
    }

    private fun getTimeAgo(date: Date?): String {
        if (date == null) return "Baru saja"
        val diff = Date().time - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days Hari yang lalu"
            hours > 0 -> "$hours Jam yang lalu"
            minutes > 0 -> "$minutes Menit yang lalu"
            else -> "Baru saja"
        }
    }

    private fun setupCategory(layoutId: Int, filterValue: String, iconRes: Int, bgColor: String) {
        val root = findViewById<View>(layoutId) ?: return

        root.findViewById<TextView>(R.id.tvCategoryName)?.text = filterValue.uppercase()
        root.findViewById<CardView>(R.id.cvCategoryIcon)?.setCardBackgroundColor(Color.parseColor(bgColor))

        val iconImg = root.findViewById<ImageView>(R.id.ivCategoryIcon)
        iconImg?.setImageResource(iconRes)
        iconImg?.clearColorFilter()

        root.setOnClickListener { handleCategoryClick(filterValue) }
    }

    override fun onResume() {
        super.onResume()
        syncUserSession()
        loadDataDashboard()
    }
}