package id.ac.umkt.mobileprogramming.uts

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPagerOnboarding)

        val adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        // --- ANIMASI PREMIUM STANDARD (DEPTH PAGE TRANSFORMER) ---
        // Menghilangkan efek geser kaku, diganti dengan efek halaman mengecil dan tenggelam ke belakang
        viewPager.setPageTransformer { page, position ->
            val minScale = 0.85f // Seberapa kecil halaman akan menyusut di belakang
            val minAlpha = 0.5f  // Seberapa redup halaman saat di belakang

            page.apply {
                val pageWidth = width
                when {
                    position < -1 -> { // Halaman jauh di kiri
                        alpha = 0f
                    }
                    position <= 0 -> { // Halaman yang sedang aktif (bergeser ke kiri)
                        alpha = 1f
                        translationX = 0f
                        translationZ = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                    position <= 1 -> { // Halaman baru yang akan masuk dari kanan
                        // Tahan halaman agar tidak bergeser secara normal
                        translationX = pageWidth * -position

                        // Buat seolah-olah halamannya ada di belakang layar
                        translationZ = -1f

                        // Animasi memudar dan mengecil
                        alpha = 1 - position
                        val scaleFactor = minScale + (1 - minScale) * (1 - abs(position))
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    else -> { // Halaman jauh di kanan
                        alpha = 0f
                    }
                }
            }
        }
    }

    // Fungsi pindah halaman lambat saat tombol diklik (Premium Smooth)
    fun moveToNextSlide() {
        val nextItem = viewPager.currentItem + 1
        if (nextItem < 3) {
            val fakeDragAnimator = ValueAnimator.ofFloat(0f, viewPager.width.toFloat())
            var lastValue = 0f

            // Durasi disesuaikan menjadi 750ms agar pas (tidak terlalu cepat, tidak membosankan)
            fakeDragAnimator.duration = 750

            // Menggunakan interpolator premium standar Material Design (Cepat di awal, melambat sangat halus di akhir)
            fakeDragAnimator.interpolator = FastOutSlowInInterpolator()

            fakeDragAnimator.addUpdateListener { valueAnimator ->
                val currentValue = valueAnimator.animatedValue as Float
                val delta = currentValue - lastValue
                viewPager.fakeDragBy(-delta)
                lastValue = currentValue
            }

            viewPager.beginFakeDrag()
            fakeDragAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    viewPager.endFakeDrag()
                }
            })
            fakeDragAnimator.start()
        }
    }
}