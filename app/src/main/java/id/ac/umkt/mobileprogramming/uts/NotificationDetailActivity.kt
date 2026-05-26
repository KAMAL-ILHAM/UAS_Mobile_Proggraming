package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.widget.FrameLayout // Pastikan ini di-import
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NotificationDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        // 1. Handle tombol back header
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Handle tombol lacak status (Ubah pemanggilan menjadi FrameLayout)
        findViewById<FrameLayout>(R.id.btnTrack).setOnClickListener {
            // Logika ketika tombol lacak ditekan
        }
    }
}