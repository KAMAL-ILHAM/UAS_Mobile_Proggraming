package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private var namaUserAktif: String = ""

    // State Data
    private var allMyReports = listOf<DocumentSnapshot>()
    private var currentActiveTab = "SEMUA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        db = FirebaseFirestore.getInstance()

        // Sesi Pengguna
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        namaUserAktif = sharedPref.getString("full_name", "")?.trim() ?: ""

        // Setup UI
        findViewById<ImageView>(R.id.btnBackRiwayat)?.setOnClickListener { finish() }
        findViewById<View>(R.id.btnMulaiMelapor)?.setOnClickListener {
            startActivity(Intent(this, BuatLaporanActivity::class.java))
            finish()
        }

        setupBottomNavigation()
        setupTabListeners()
        fetchRiwayatData()
    }

    // Penarikan Data Utama
    private fun fetchRiwayatData() {
        if (namaUserAktif.isEmpty()) return

        val layoutEmptyState: View? = findViewById(R.id.layoutEmptyState)
        val layoutHasData: View? = findViewById(R.id.layoutHasData)

        db.collection("laporan")
            .whereEqualTo("nama_pelapor", namaUserAktif)
            .get()
            .addOnSuccessListener { documents ->
                allMyReports = documents.documents

                if (allMyReports.isEmpty()) {
                    layoutEmptyState?.visibility = View.VISIBLE
                    layoutHasData?.visibility = View.GONE
                } else {
                    layoutEmptyState?.visibility = View.GONE
                    layoutHasData?.visibility = View.VISIBLE

                    updateTabCounts()
                    applyTabFilter(currentActiveTab)
                }
            }
    }

    // Manajemen Tab
    private fun setupTabListeners() {
        findViewById<TextView>(R.id.btnTabSemua)?.setOnClickListener { applyTabFilter("SEMUA") }
        findViewById<TextView>(R.id.btnTabDiproses)?.setOnClickListener { applyTabFilter("DIPROSES") }
        findViewById<TextView>(R.id.btnTabSelesai)?.setOnClickListener { applyTabFilter("SELESAI") }
        findViewById<TextView>(R.id.btnTabDitolak)?.setOnClickListener { applyTabFilter("DITOLAK") }
    }

    private fun updateTabCounts() {
        val countSemua = allMyReports.size
        val countSelesai = allMyReports.count { (it.getString("status") ?: "").contains("SELESAI", true) }
        val countDitolak = allMyReports.count {
            val status = (it.getString("status") ?: "").uppercase()
            status.contains("BATAL") || status.contains("TOLAK")
        }
        val countDiproses = countSemua - countSelesai - countDitolak

        findViewById<TextView>(R.id.btnTabSemua)?.text = if (countSemua > 0) "Semua ($countSemua)" else "Semua"
        findViewById<TextView>(R.id.btnTabDiproses)?.text = if (countDiproses > 0) "Diproses ($countDiproses)" else "Diproses"
        findViewById<TextView>(R.id.btnTabSelesai)?.text = if (countSelesai > 0) "Selesai ($countSelesai)" else "Selesai"
        findViewById<TextView>(R.id.btnTabDitolak)?.text = if (countDitolak > 0) "Ditolak ($countDitolak)" else "Ditolak"
    }

    private fun applyTabFilter(selectedTab: String) {
        currentActiveTab = selectedTab
        updateTabVisuals()

        val filteredList = when (selectedTab) {
            "SELESAI" -> allMyReports.filter { (it.getString("status") ?: "").contains("SELESAI", true) }
            "DITOLAK" -> allMyReports.filter {
                val status = (it.getString("status") ?: "").uppercase()
                status.contains("BATAL") || status.contains("TOLAK")
            }
            "DIPROSES" -> allMyReports.filter {
                val status = (it.getString("status") ?: "").uppercase()
                !status.contains("SELESAI") && !status.contains("BATAL") && !status.contains("TOLAK")
            }
            else -> allMyReports
        }

        val sortedList = filteredList.sortedByDescending {
            it.getTimestamp("timestamp")?.toDate()?.time ?: 0L
        }

        renderRiwayatList(sortedList)
    }

    private fun updateTabVisuals() {
        val tabs = mapOf(
            "SEMUA" to findViewById<TextView>(R.id.btnTabSemua),
            "DIPROSES" to findViewById<TextView>(R.id.btnTabDiproses),
            "SELESAI" to findViewById<TextView>(R.id.btnTabSelesai),
            "DITOLAK" to findViewById<TextView>(R.id.btnTabDitolak)
        )

        for ((tabName, textView) in tabs) {
            if (tabName == currentActiveTab) {
                textView?.setBackgroundResource(R.drawable.bg_rounded_blue_light)
                textView?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0F172A"))
                textView?.setTextColor(Color.WHITE)
            } else {
                textView?.setBackgroundResource(R.drawable.bg_input_modern)
                textView?.backgroundTintList = null
                textView?.setTextColor(Color.parseColor("#64748B"))
            }
        }
    }

    // Render Data UI
    private fun renderRiwayatList(dataList: List<DocumentSnapshot>) {
        val containerList = findViewById<LinearLayout>(R.id.containerRiwayatList)
        containerList?.removeAllViews()

        val inflater = LayoutInflater.from(this)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        for (dokumen in dataList) {
            val viewItem = inflater.inflate(R.layout.item_riwayat, containerList, false)

            val currentDocId = dokumen.id
            val currentDocTiket = dokumen.getString("nomor_tiket") ?: ""
            val dbKategori = dokumen.getString("kategori") ?: "Fasilitas"
            val dbStatus = dokumen.getString("status") ?: "SEDANG DIPROSES"
            val dbDetail = dokumen.getString("detail") ?: dbKategori

            val date = dokumen.getTimestamp("timestamp")?.toDate()
            val dateString = if (date != null) dateFormat.format(date) else "-"
            val shortDetail = if (dbDetail.length > 25) "${dbDetail.take(25)}..." else dbDetail

            val tvJudul = viewItem.findViewById<TextView>(R.id.tvRiwayatTitle)
            val tvSub = viewItem.findViewById<TextView>(R.id.tvRiwayatDesc)
            val tvStatus = viewItem.findViewById<TextView>(R.id.tvRiwayatStatus)
            val ivIcon = viewItem.findViewById<ImageView>(R.id.ivRiwayatIcon)
            val llStatus = viewItem.findViewById<LinearLayout>(R.id.llRiwayatStatus)

            tvJudul?.text = shortDetail
            tvSub?.text = dateString
            tvSub?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0)
            tvSub?.compoundDrawablePadding = 12

            tvStatus?.text = dbStatus.replace("● ", "")
            val bgDrawable = GradientDrawable().apply { cornerRadius = 20f }

            when {
                dbStatus.contains("SELESAI", ignoreCase = true) -> {
                    tvStatus?.setTextColor(Color.parseColor("#10B981"))
                    bgDrawable.setColor(Color.parseColor("#E6F9F3"))
                }
                dbStatus.contains("BATAL", ignoreCase = true) || dbStatus.contains("TOLAK", ignoreCase = true) -> {
                    tvStatus?.setTextColor(Color.parseColor("#EF4444"))
                    bgDrawable.setColor(Color.parseColor("#FEE2E2"))
                }
                else -> {
                    tvStatus?.setTextColor(Color.parseColor("#2563EB"))
                    bgDrawable.setColor(Color.parseColor("#EFF6FF"))
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

            viewItem.setOnClickListener {
                val intent = Intent(this, DetailRiwayatActivity::class.java)
                intent.putExtra("DOCUMENT_ID", currentDocId)
                intent.putExtra("NOMOR_TIKET", currentDocTiket)
                startActivity(intent)
            }

            containerList?.addView(viewItem)
        }
    }

    // Navigasi Bawah
    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.navBeranda)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        findViewById<LinearLayout>(R.id.navNotifikasi)?.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navProfil)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            finish()
        }
    }
}