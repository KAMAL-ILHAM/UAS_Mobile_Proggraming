package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        val data = getDummyData()
        val adapter = NotificationAdapter(data)
        rvNotifications.adapter = adapter
    }

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