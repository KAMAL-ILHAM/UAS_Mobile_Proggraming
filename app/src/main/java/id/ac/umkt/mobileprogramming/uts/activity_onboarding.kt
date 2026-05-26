package id.ac.umkt.mobileprogramming.uts

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
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

        // --- ANIMASI HYBRID PREMIUM: HORIZONTAL (1-2) & VERTICAL (3) ---
        viewPager.setPageTransformer { page, position ->
            page.apply {
                val pageWidth = width
                val pageHeight = height

                when {
                    position < -1 -> alpha = 0f
                    position <= 1 -> {
                        // LOGIKA UNIK: Deteksi transisi ke/dari halaman terakhir (index 2)
                        val isTransitioningToOrFromLastPage = (viewPager.currentItem == 1 && position > 0) || (viewPager.currentItem == 2 && position < 0) || viewPager.currentItem == 2

                        if (isTransitioningToOrFromLastPage) {
                            // !!! EFEK KE ATAS !!!
                            // 1. Tahan pergerakan horizontal default agar tidak geser kanan/kiri
                            translationX = pageWidth * -position

                            // 2. Ubah menjadi pergerakan vertikal (naik/turun)
                            translationY = position * pageHeight
                        } else {
                            // !!! EFEK SAMPING !!!
                            // Halaman 1 dan 2 tetap horizontal biasa
                            translationX = 0f
                            translationY = 0f
                        }

                        // Fade & Scale yang sangat halus agar transisi terasa "mahal"
                        alpha = 1 - abs(position)
                        val scaleFactor = 0.95f + (1 - abs(position)) * 0.05f
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    else -> alpha = 0f
                }
            }
        }
    }

    // Fungsi pindah halaman lambat saat tombol diklik (Smooth & Slow)
    fun moveToNextSlide() {
        val nextItem = viewPager.currentItem + 1
        if (nextItem < 3) {
            // Kita buat fake drag kustom agar durasiperpindahannya lambat
            val fakeDragAnimator = ValueAnimator.ofFloat(0f, viewPager.width.toFloat())
            var lastValue = 0f

            fakeDragAnimator.duration = 950 // Durasi hampir 1 detik agar sangat lambat
            fakeDragAnimator.interpolator = AccelerateDecelerateInterpolator()

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