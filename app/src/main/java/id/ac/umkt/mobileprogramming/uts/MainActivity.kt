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
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var namaUserAktif: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        // --- 1. SETUP KATEGORI ---
        setupCategory(R.id.cat1, "ELEKTRONIK", R.drawable.ic_monitor, "#EBF2FF")
        setupCategory(R.id.cat2, "FURNITUR", R.drawable.ic_furnitur, "#FFF4E5")
        setupCategory(R.id.cat3, "SANITASI", R.drawable.ic_sanitasi, "#E6F9F3")
        setupCategory(R.id.cat4, "JARINGAN", R.drawable.ic_jaringan, "#F3E8FF")
        setupCategory(R.id.cat5, "GEDUNG", R.drawable.ic_gedung, "#FFE4E6")
        setupCategory(R.id.cat6, "AREA LUAR", R.drawable.ic_outdoor, "#F0FDF4")

        // --- 2. TARIK DATA DINAMIS ---
        syncUserSession()
        loadDataDashboard()

        // --- 3. NAVIGASI KLIK ---
        findViewById<FrameLayout>(R.id.btnProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        findViewById<View>(R.id.navProfil)?.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        findViewById<View>(R.id.navNotifikasi)?.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        findViewById<CardView>(R.id.cardReport)?.setOnClickListener {
            startActivity(Intent(this, DetailRiwayatActivity::class.java))
        }

        findViewById<TextView>(R.id.btnSeeAll)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            intent.putExtra("EXTRA_IS_EMPTY", false)
            startActivity(intent)
        }

        findViewById<View>(R.id.navRiwayat)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            intent.putExtra("EXTRA_IS_EMPTY", true)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardBuatLaporan)?.setOnClickListener {
            startActivity(Intent(this, BuatLaporanActivity::class.java))
        }
    }

    private fun syncUserSession() {
        val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        var namaLengkap = sharedPref.getString("full_name", "")

        if (namaLengkap.isNullOrEmpty()) {
            namaLengkap = intent.getStringExtra("USER_NAME") ?: "User Tidak Dikenal"
        }

        // Ambil nama tanpa spasi liar di ujung untuk pencarian di loadDataDashboard
        namaUserAktif = namaLengkap.trim()

        val major = sharedPref.getString("major", "") ?: ""
        val phone = sharedPref.getString("phone", "") ?: ""
        val badgeProfil = findViewById<View>(R.id.badgeProfilRedDot)

        if (major.isEmpty() || phone.isEmpty()) {
            badgeProfil?.visibility = View.VISIBLE
        } else {
            badgeProfil?.visibility = View.GONE
        }

        val inisial = namaLengkap.split(" ")
            .filter { it.isNotEmpty() }
            .map { it[0] }
            .joinToString("")
            .take(2).uppercase()

        findViewById<TextView>(R.id.tvGreeting)?.text = "Halo, $namaLengkap !"
        findViewById<TextView>(R.id.tvMajor)?.text = if (major.isEmpty()) "PRODI BELUM DIISI" else major
        findViewById<TextView>(R.id.tvInitials)?.text = inisial
    }

    /**
     * FUNGSI B: TARIK DATA DASHBOARD DENGAN STRATEGI DOUBLE DEFENSE
     */
    private fun loadDataDashboard() {
        val cardReport = findViewById<CardView>(R.id.cardReport)
        val containerRiwayat = findViewById<View>(R.id.containerRiwayat)

        // LAPIS 1: Ambil dari cache lokal dulu agar card langsung diam kokoh (Anti-Kedip)
        val sharedPrefLokal = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
        val localKategori = sharedPrefLokal.getString("kategori", null)
        val localLokasi = sharedPrefLokal.getString("lokasi", "Gedung UMKT")
        val localStatus = sharedPrefLokal.getString("status", "● SEDANG DIPROSES")

        if (localKategori != null) {
            cardReport?.visibility = View.VISIBLE
            containerRiwayat?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvReportStatus)?.apply {
                text = localStatus
                setTextColor(Color.parseColor("#FBBC05"))
            }
            findViewById<TextView>(R.id.tvReportTitle)?.text = localKategori
            findViewById<TextView>(R.id.tvReportLocation)?.text = localLokasi
        } else {
            // Jika cache lokal kosong, sembunyikan dulu sambil menunggu respon Firebase
            cardReport?.visibility = View.GONE
            containerRiwayat?.visibility = View.GONE
        }

        // Validasi identitas user aktif
        if (namaUserAktif.isEmpty() || namaUserAktif == "User Tidak Dikenal") {
            return
        }

        // LAPIS 2: Tarik data dari Firebase & saring secara toleran di memori (Kebal Huruf Kapital & Spasi)
        db.collection("laporan")
            .get()
            .addOnSuccessListener { documents ->
                // Filter dokumen secara aman menggunakan .trim() dan ignoreCase
                val laporanSaya = documents.filter { doc ->
                    val pelapor = doc.getString("nama_pelapor")?.trim() ?: ""
                    pelapor.equals(namaUserAktif, ignoreCase = true)
                }

                if (laporanSaya.isNotEmpty()) {
                    // Cari laporan terbaru milik user tersebut berdasarkan timestamp terbesar
                    val dokumenTerbaru = laporanSaya.maxByOrNull {
                        it.getTimestamp("timestamp")?.toDate()?.time ?: 0L
                    }

                    if (dokumenTerbaru != null) {
                        val dbKategori = dokumenTerbaru.getString("kategori") ?: "Kategori Tidak Diketahui"
                        val dbLokasi = dokumenTerbaru.getString("lokasi") ?: "Lokasi Tidak Diketahui"
                        val dbStatus = dokumenTerbaru.getString("status") ?: "● SEDANG DIPROSES"

                        // Tampilkan card secara aman
                        cardReport?.visibility = View.VISIBLE
                        containerRiwayat?.visibility = View.VISIBLE

                        // Suntikkan data up-to-date dari server ke UI layar
                        findViewById<TextView>(R.id.tvReportStatus)?.apply {
                            text = dbStatus
                            if (dbStatus.contains("SELESAI", ignoreCase = true)) {
                                setTextColor(Color.parseColor("#2ECC71")) // Hijau jika selesai
                            } else {
                                setTextColor(Color.parseColor("#FBBC05")) // Kuning warning
                            }
                        }
                        findViewById<TextView>(R.id.tvReportTitle)?.text = dbKategori
                        findViewById<TextView>(R.id.tvReportLocation)?.text = dbLokasi

                        // Perbarui cache lokal agar tetap sinkron untuk sesi berikutnya
                        with(sharedPrefLokal.edit()) {
                            putString("kategori", dbKategori)
                            putString("lokasi", dbLokasi)
                            putString("status", dbStatus)
                            apply()
                        }
                    }
                } else {
                    // Jika di database Firestore benar-benar kosong / tidak ada laporan untuk user ini
                    cardReport?.visibility = View.GONE
                    containerRiwayat?.visibility = View.GONE

                    // Bersihkan cache lokal karena datanya sudah bersih di server
                    sharedPrefLokal.edit().clear().apply()
                }
            }
            .addOnFailureListener {
                // Jika koneksi internet bermasalah, biarkan data cache lokal (jika ada) tetap tampil menemani user
                if (localKategori == null) {
                    cardReport?.visibility = View.GONE
                    containerRiwayat?.visibility = View.GONE
                }
            }
    }

    private fun setupCategory(layoutId: Int, title: String, iconRes: Int, bgColor: String) {
        val root = findViewById<View>(layoutId) ?: return
        root.findViewById<TextView>(R.id.tvCategoryName)?.text = title
        root.findViewById<CardView>(R.id.cvCategoryIcon)?.setCardBackgroundColor(Color.parseColor(bgColor))

        val iconImg = root.findViewById<ImageView>(R.id.ivCategoryIcon)
        iconImg?.setImageResource(iconRes)
        iconImg?.clearColorFilter()
    }

    override fun onResume() {
        super.onResume()
        syncUserSession()
        loadDataDashboard()
    }
}