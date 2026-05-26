package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class RiwayatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        // 1. Kenalkan kedua layout penampung dari XML
        val layoutEmptyState: View = findViewById(R.id.layoutEmptyState)
        val layoutHasData: View = findViewById(R.id.layoutHasData)

        // 2. TANGKAP SINYAL DARI MAIN ACTIVITY
        // "EXTRA_IS_EMPTY" dikirim dari MainActivity. Jika tidak ada sinyal, defaultnya true (kosong)
        val isEmpty = intent.getBooleanExtra("EXTRA_IS_EMPTY", true)

        // 3. Logika Penampilan Halaman
        if (isEmpty) {
            // Jika sinyalnya KOSONG (dari klik ikon bawah di MainActivity)
            layoutEmptyState.visibility = View.VISIBLE
            layoutHasData.visibility = View.GONE
        } else {
            // Jika sinyalnya ADA DATA (dari klik tulisan "LIHAT SEMUA" di MainActivity)
            layoutEmptyState.visibility = View.GONE
            layoutHasData.visibility = View.VISIBLE
        }

        // 4. Tombol Back
        val btnBack: ImageView = findViewById(R.id.btnBackRiwayat)
        btnBack.setOnClickListener {
            finish()
        }

        // 5. Tombol Mulai Melapor (Muncul di layar kosong)
        // Menggunakan View agar 100% aman dari force close
        val btnMulaiMelapor: View = findViewById(R.id.btnMulaiMelapor)
        btnMulaiMelapor.setOnClickListener {
            // BAGIAN YANG DITAMBAHKAN: Arahkan ke Form Laporan Baru
            val intent = Intent(this, BuatLaporanActivity::class.java)
            startActivity(intent)
        }

        // 6. Klik Detail AC (Muncul di layar ada data)
        val cardAC: CardView? = findViewById(R.id.cardReportAC)
        cardAC?.setOnClickListener {
            val intent = Intent(this, DetailRiwayatActivity::class.java)
            startActivity(intent)
        }

        // 7. Setup Navigasi Bawah
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        findViewById<LinearLayout>(R.id.navBeranda).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish() // Tutup halaman riwayat agar tidak menumpuk
        }

        findViewById<LinearLayout>(R.id.navNotifikasi).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.navProfil).setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
            finish()
        }

        // Catatan: navRiwayat tidak perlu diberi aksi klik karena user SUDAH berada di halaman Riwayat.
    }
}