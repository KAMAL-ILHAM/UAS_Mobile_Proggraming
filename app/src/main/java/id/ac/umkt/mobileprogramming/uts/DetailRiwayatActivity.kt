package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailRiwayatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_riwayat)

        // 1. Kenalkan tombol back
        val btnBack: FrameLayout = findViewById(R.id.btnBackDetail)
        btnBack.setOnClickListener {
            finish()
        }

        // 2. Tarik data dari Database Lokal Sementara
        val sharedPref = getSharedPreferences("DB_LOKAL_SEMENTARA", Context.MODE_PRIVATE)
        val savedKategori = sharedPref.getString("kategori", "Fasilitas Umum")
        // Catatan: Karena di form awal nggak ada ID Laporan, kita buat format ID sederhana pakai Waktu
        val fakeId = "EIO-${System.currentTimeMillis().toString().takeLast(4)}"
        val savedStatus = sharedPref.getString("status", "SELESAI") // Sesuai dengan desain badgeStatusDetail
        val savedDetail = sharedPref.getString("detail", "Tidak ada deskripsi tambahan.")

        // 3. Suntikkan data ke View XML (Sudah Disesuaikan dengan XML kamu)
        findViewById<TextView>(R.id.tvDetailTitle)?.text = savedKategori
        findViewById<TextView>(R.id.tvReportId)?.text = "ID: $fakeId"

        // Membersihkan format status (misal sebelumnya "● SEDANG DIPROSES" jadi "SEDANG DIPROSES")
        val cleanStatus = savedStatus?.replace("● ", "") ?: "DIPROSES"
        findViewById<TextView>(R.id.badgeStatusDetail)?.text = cleanStatus

        val parentLayout = findViewById<android.widget.LinearLayout>(R.id.bottomNavDetail).parent as androidx.constraintlayout.widget.ConstraintLayout
        val scrollView = parentLayout.getChildAt(1) as android.widget.ScrollView
        val linearLayout = scrollView.getChildAt(0) as android.widget.LinearLayout
        val textDetailNode = linearLayout.getChildAt(1) as TextView
        textDetailNode.text = "\"$savedDetail\""

        val textLokasiNode = linearLayout.getChildAt(0) as TextView
        val savedLokasi = sharedPref.getString("lokasi", "Gedung UMKT")
        textLokasiNode.text = savedLokasi
    }
}