package id.ac.umkt.mobileprogramming.uts
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class DetailRiwayatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_riwayat)

        // Mengenalkan tombol back
        val btnBack: FrameLayout = findViewById(R.id.btnBackDetail)

        // Jika diklik, tutup halaman ini dan kembali ke Riwayat
        btnBack.setOnClickListener {
            finish()
        }
    }
}