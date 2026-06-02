package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class NotificationDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        // 1. Tangkap Data Asli dari Intent Adapter
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Detail Pemberitahuan"
        val time = intent.getStringExtra("EXTRA_TIME") ?: "Baru saja"
        val desc = intent.getStringExtra("EXTRA_DESC") ?: "Tidak ada detail tambahan."
        val tiket = intent.getStringExtra("EXTRA_TIKET") ?: "#EIO-0000"
        val kategori = intent.getStringExtra("EXTRA_KATEGORI") ?: "-"
        val lokasi = intent.getStringExtra("EXTRA_LOKASI") ?: "-"
        val documentId = intent.getStringExtra("EXTRA_DOC_ID") ?: ""

        // 2. Kenalkan View dan Suntik Data ke Layar
        findViewById<TextView>(R.id.tvTitleDetail)?.text = title
        findViewById<TextView>(R.id.tvTimeDetail)?.text = time.uppercase()
        findViewById<TextView>(R.id.tvDescDetail)?.text = desc

        findViewById<TextView>(R.id.tvIdLaporan)?.text = tiket
        findViewById<TextView>(R.id.tvFasilitas)?.text = kategori
        findViewById<TextView>(R.id.tvLokasi)?.text = lokasi

        // 3. Update Ikon Dinamis berdasarkan status (Opsional untuk estetika maksimal)
        val ivIconDetail = findViewById<ImageView>(R.id.ivIconDetail)
        val bgIconDetail = findViewById<FrameLayout>(R.id.bgIconDetail)

        if (title.contains("Selesai", true)) {
            ivIconDetail?.setImageResource(R.drawable.ic_check_circle)
            bgIconDetail?.setBackgroundResource(R.drawable.bg_circle_green) // Pastikan drawable ini ada, atau pakai colorTint
        } else if (title.contains("Batal", true)) {
            ivIconDetail?.setImageResource(R.drawable.ic_cancel)
            // Warna merah
        } else {
            // Default biru/kuning seperti biasa
            ivIconDetail?.setImageResource(R.drawable.ic_document)
        }

        // 4. Handle tombol back header
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 5. Handle tombol lacak status
        findViewById<FrameLayout>(R.id.btnTrack).setOnClickListener {
            val trackIntent = Intent(this, DetailRiwayatActivity::class.java)
            trackIntent.putExtra("DOCUMENT_ID", documentId)
            trackIntent.putExtra("NOMOR_TIKET", tiket)
            startActivity(trackIntent)
        }
    }
}