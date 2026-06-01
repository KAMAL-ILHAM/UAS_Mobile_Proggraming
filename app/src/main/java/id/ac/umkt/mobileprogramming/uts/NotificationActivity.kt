package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.view.View

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        // Kita panggil fungsi dinamis yang baru
        val data = getDynamicData()
        val adapter = NotificationAdapter(data)
        rvNotifications.adapter = adapter

        setupBottomNavigation()
    }

    // [LOGIKA BARU] Fungsi untuk menggabungkan data dummy dengan data laporan real-time
    private fun getDynamicData(): List<NotificationItem> {
        // 1. Ambil data dummy bawaan sebagai dasar
        val baseData = getDummyData().toMutableList()

        // 2. Cek apakah ada laporan baru dari memori HP
        val sharedPref = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
        val savedKategori = sharedPref.getString("kategori", null)

        // 3. Jika ada laporan, buatkan notifikasinya dan taruh di paling atas
        if (savedKategori != null) {
            val savedLokasi = sharedPref.getString("lokasi", "Area Kampus")

            val newNotification = NotificationItem.Data(
                title = "Laporan Berhasil Diterima",
                desc = "Pengaduan $savedKategori di $savedLokasi telah masuk ke sistem kami dan sedang dalam antrean pengecekan teknisi.",
                time = "Baru saja",
                isUnread = true,
                iconRes = R.drawable.ic_document, // Menggunakan ikon dokumen yang sudah ada
                iconBgColor = R.color.bgIconBlue // Warna background biru agar menonjol
            )

            // Masukkan notifikasi baru di urutan ke-2 (Index 1), tepat di bawah tulisan Header "BARU (HARI INI)"
            baseData.add(1, newNotification)
        }

        return baseData
    }

    private fun setupBottomNavigation() {
        // Klik Beranda
        findViewById<View>(R.id.navBeranda)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Klik Riwayat
        findViewById<View>(R.id.navRiwayat)?.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Klik Profil
        findViewById<View>(R.id.navProfil)?.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Ini fungsi dummy data bawaanmu yang tidak kuubah isinya sama sekali
    private fun getDummyData(): List<NotificationItem> {
        return listOf(
            NotificationItem.Header("BARU (HARI INI)"),
            NotificationItem.Data(
                title = "Teknisi Menuju Lokasi",
                desc = "Bpk. Sumardi sedang menuju Gedung D untuk memeriksa AC Lab Komputer 2. Laporan #EIO-8821.",
                time = "10:30",
                isUnread = true,
                iconRes = R.drawable.ic_technician,
                iconBgColor = R.color.bgIconYellow
            ),
            NotificationItem.Data(
                title = "Pemeliharaan Server",
                desc = "Layanan pelaporan akan dihentikan sementara malam ini pukul 23:00 - 02:00 WITA.",
                time = "08:00",
                isUnread = true,
                iconRes = R.drawable.ic_speaker,
                iconBgColor = R.color.bgIconBlue
            ),
            NotificationItem.Header("SEBELUMNYA"),
            NotificationItem.Data(
                title = "Laporan Selesai Diperbaiki",
                desc = "Perbaikan Lampu Koridor Gedung E telah selesai. Berikan penilaian Anda untuk teknisi.",
                time = "Kemarin",
                isUnread = false,
                iconRes = R.drawable.ic_check_circle,
                iconBgColor = R.color.bgIconGreen
            ),
            NotificationItem.Data(
                title = "Laporan Diterima",
                desc = "Laporan kerusakan Kran Air (#EIO-8705) telah tervalidasi dan masuk antrean perbaikan.",
                time = "22 Apr",
                isUnread = false,
                iconRes = R.drawable.ic_document,
                iconBgColor = R.color.bgIconGray
            ),
            NotificationItem.Data(
                title = "Laporan Dibatalkan",
                desc = "Laporan Proyektor Blur (#EIO-8612) dibatalkan.",
                time = "20 Apr",
                isUnread = false,
                iconRes = R.drawable.ic_cancel,
                iconBgColor = R.color.bgIconRed
            )
        )
    }


}