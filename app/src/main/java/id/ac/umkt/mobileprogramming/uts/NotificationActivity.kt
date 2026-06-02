package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationActivity : AppCompatActivity() {

    private lateinit var adapter: NotificationAdapter
    private var allRawData = mutableListOf<NotificationItem.Data>()
    private var currentFilter = "SEMUA"
    private lateinit var db: FirebaseFirestore

    // Nama file untuk SharedPreferences khusus Notifikasi
    private val PREFS_NOTIF = "NOTIF_PREFS"
    private val KEY_READ_IDS = "READ_IDS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        db = FirebaseFirestore.getInstance()

        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(emptyList()) { clickedItem ->
            if (clickedItem.isUnread) {
                clickedItem.isUnread = false
                // Simpan ID dokumen ini ke memori lokal agar status "dibaca"-nya permanen
                saveReadNotification(clickedItem.documentId)
                applyFilter(currentFilter)
            }
        }
        rvNotifications.adapter = adapter

        setupBottomNavigation()
        setupFilterLogic()

        fetchRealNotifications()
    }

    // --- FUNGSI MEMORI LOKAL (Baru) ---
    private fun getReadNotifications(): MutableSet<String> {
        val prefs = getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE)
        // Ambil data set yang ada, atau buat set kosong jika belum ada
        return prefs.getStringSet(KEY_READ_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    private fun saveReadNotification(docId: String) {
        val prefs = getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE)
        val currentReadIds = getReadNotifications()
        currentReadIds.add(docId)
        prefs.edit().putStringSet(KEY_READ_IDS, currentReadIds).apply()
    }

    private fun saveAllReadNotifications(docIds: List<String>) {
        val prefs = getSharedPreferences(PREFS_NOTIF, Context.MODE_PRIVATE)
        val currentReadIds = getReadNotifications()
        currentReadIds.addAll(docIds)
        prefs.edit().putStringSet(KEY_READ_IDS, currentReadIds).apply()
    }
    // -----------------------------------

    private fun fetchRealNotifications() {
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val namaUserAktif = sharedPref.getString("full_name", "")?.trim() ?: ""

        if (namaUserAktif.isEmpty()) return

        db.collection("laporan")
            .whereEqualTo("nama_pelapor", namaUserAktif)
            .get()
            .addOnSuccessListener { documents ->
                val realDataList = mutableListOf<NotificationItem.Data>()

                // Ambil daftar ID yang sudah pernah dibaca dari memori HP
                val readIds = getReadNotifications()

                // Notifikasi Sistem Dummy
                val isSystemRead = readIds.contains("SYS-001")
                realDataList.add(
                    NotificationItem.Data(
                        title = "Sistem Terhubung",
                        desc = "Layanan sinkronisasi cloud berjalan normal. Data aman.",
                        time = "08:00",
                        isUnread = !isSystemRead, // Cek status baca
                        iconRes = R.drawable.ic_speaker,
                        iconBgColor = R.color.bgIconBlue,
                        documentId = "SYS-001" // ID Khusus Dummy
                    )
                )

                val sortedDocs = documents.sortedByDescending { it.getTimestamp("timestamp")?.toDate()?.time ?: 0L }

                for (doc in sortedDocs) {
                    val status = doc.getString("status") ?: "SEDANG DIPROSES"
                    val kategori = doc.getString("kategori") ?: "Fasilitas"
                    val lokasi = doc.getString("lokasi") ?: "Kampus"
                    val tiket = doc.getString("nomor_tiket") ?: "---"
                    val date = doc.getTimestamp("timestamp")?.toDate()

                    val timeString = if (date != null) {
                        val today = Calendar.getInstance()
                        val docDate = Calendar.getInstance().apply { time = date }

                        if (today.get(Calendar.YEAR) == docDate.get(Calendar.YEAR) &&
                            today.get(Calendar.DAY_OF_YEAR) == docDate.get(Calendar.DAY_OF_YEAR)) {
                            "Hari ini, " + SimpleDateFormat("HH:mm", Locale("id", "ID")).format(date)
                        } else {
                            SimpleDateFormat("dd MMM", Locale("id", "ID")).format(date)
                        }
                    } else {
                        "Baru saja"
                    }

                    val (iconRes, iconBg) = when {
                        status.contains("SELESAI", true) -> Pair(R.drawable.ic_check_circle, R.color.bgIconGreen)
                        status.contains("BATAL", true) -> Pair(R.drawable.ic_cancel, R.color.bgIconRed)
                        status.contains("TEKNISI", true) -> Pair(R.drawable.ic_technician, R.color.bgIconYellow)
                        else -> Pair(R.drawable.ic_document, R.color.bgIconBlue)
                    }

                    // Logika penentu: Cek apakah ID dokumen dari Firebase ada di memori HP
                    val isDocRead = readIds.contains(doc.id)

                    realDataList.add(
                        NotificationItem.Data(
                            title = "Update Status: $kategori",
                            desc = "Laporan $tiket di $lokasi saat ini $status.",
                            time = timeString,
                            isUnread = !isDocRead, // Jika belum ada di memori, berarti belum dibaca
                            iconRes = iconRes,
                            iconBgColor = iconBg,
                            documentId = doc.id,
                            tiket = tiket,
                            kategori = kategori,
                            lokasi = lokasi,
                            status = status
                        )
                    )
                }

                allRawData = realDataList
                applyFilter(currentFilter)
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun setupFilterLogic() {
        val btnSemua = findViewById<TextView>(R.id.btnTabSemua)
        val btnBelumDibaca = findViewById<TextView>(R.id.btnTabBelumDibaca)
        val btnSistem = findViewById<TextView>(R.id.btnTabSistem)
        val btnMarkAllRead = findViewById<ImageButton>(R.id.btnMarkRead)

        updateUnreadBadge()

        btnSemua?.setOnClickListener { applyFilter("SEMUA") }
        btnBelumDibaca?.setOnClickListener { applyFilter("BELUM_DIBACA") }
        btnSistem?.setOnClickListener { applyFilter("SISTEM") }

        // Fitur Tandai Semua Dibaca
        btnMarkAllRead?.setOnClickListener {
            // Ambil semua ID yang ada di list saat ini
            val allIds = allRawData.map { it.documentId }
            // Simpan semua ID tersebut ke memori lokal
            saveAllReadNotifications(allIds)

            // Ubah UI
            allRawData.forEach { it.isUnread = false }
            applyFilter(currentFilter)
        }
    }

    private fun applyFilter(filterType: String) {
        currentFilter = filterType

        val filteredData = when (filterType) {
            "BELUM_DIBACA" -> allRawData.filter { it.isUnread }
            "SISTEM" -> allRawData.filter {
                it.title.contains("Sistem", true) ||
                        it.title.contains("Server", true) ||
                        it.title.contains("Pemeliharaan", true)
            }
            else -> allRawData
        }

        adapter.updateData(groupDataWithHeaders(filteredData))
        updateUnreadBadge()
        updateTabVisuals()
    }

    private fun updateTabVisuals() {
        val btnSemua = findViewById<TextView>(R.id.btnTabSemua)
        val btnBelumDibaca = findViewById<TextView>(R.id.btnTabBelumDibaca)
        val btnSistem = findViewById<TextView>(R.id.btnTabSistem)

        val tabs = listOf(btnSemua, btnBelumDibaca, btnSistem)
        tabs.forEach {
            it?.setBackgroundResource(R.drawable.bg_tab_inactive)
            it?.setTextColor(ContextCompat.getColor(this, R.color.textSecondary))
        }

        val activeTab = when(currentFilter) {
            "BELUM_DIBACA" -> btnBelumDibaca
            "SISTEM" -> btnSistem
            else -> btnSemua
        }

        activeTab?.setBackgroundResource(R.drawable.bg_tab_active)
        activeTab?.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
    }

    private fun groupDataWithHeaders(dataList: List<NotificationItem.Data>): List<NotificationItem> {
        val groupedList = mutableListOf<NotificationItem>()

        val baru = dataList.filter { it.time.contains("Hari ini", true) || it.time.contains("Baru", true) || it.time.contains("08:00") }
        val sebelumnya = dataList.filter { !it.time.contains("Hari ini", true) && !it.time.contains("Baru", true) && !it.time.contains("08:00") }

        if (baru.isNotEmpty()) {
            groupedList.add(NotificationItem.Header("BARU (HARI INI)"))
            groupedList.addAll(baru)
        }
        if (sebelumnya.isNotEmpty()) {
            groupedList.add(NotificationItem.Header("SEBELUMNYA"))
            groupedList.addAll(sebelumnya)
        }

        return groupedList
    }

    private fun updateUnreadBadge() {
        val unreadCount = allRawData.count { it.isUnread }
        val tvTabBelumDibaca = findViewById<TextView>(R.id.btnTabBelumDibaca)

        if (unreadCount > 0) {
            tvTabBelumDibaca?.text = "Belum Dibaca ($unreadCount)"
        } else {
            tvTabBelumDibaca?.text = "Belum Dibaca"
        }
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.navBeranda)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        findViewById<LinearLayout>(R.id.navRiwayat)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<LinearLayout>(R.id.navProfil)?.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}