package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView // Tambahan import untuk CardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Deklarasi elemen berdasarkan ID dari XML
        val btnProfile = findViewById<FrameLayout>(R.id.btnProfile)

        // PERBAIKAN: Ubah RelativeLayout menjadi CardView
        val cardReport = findViewById<CardView>(R.id.cardReport)

        val btnSeeAll = findViewById<TextView>(R.id.btnSeeAll)

        // 2. Aksi ketika Foto Profil diklik
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
        }

        // 3. Aksi ketika Card Biru diklik
        cardReport.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        // 4. Aksi ketika teks "LIHAT SEMUA" diklik
        btnSeeAll.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
        }
    }
}