package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PusatBantuanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pusat_bantuan)

        // 1. Tombol Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 2. Setup Animasi Masuk Halaman (Slide up halus)
        val layoutMainContent = findViewById<LinearLayout>(R.id.layoutMainContent)
        layoutMainContent.translationY = 80f
        layoutMainContent.alpha = 0f
        layoutMainContent.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // 3. Setup Interaksi Expandable FAQ
        val faqListContainer = findViewById<LinearLayout>(R.id.faqListContainer)

        setupFaqInteraction(
            faqListContainer,
            findViewById(R.id.faq1Container),
            findViewById(R.id.faq1Header),
            findViewById(R.id.tvAnswer1),
            findViewById(R.id.ivArrow1),
            isExpandedInitially = true // FAQ 1 terbuka default dari Figma
        )

        setupFaqInteraction(
            faqListContainer,
            findViewById(R.id.faq2Container),
            findViewById(R.id.faq2Header),
            findViewById(R.id.tvAnswer2),
            findViewById(R.id.ivArrow2),
            isExpandedInitially = false
        )

        setupFaqInteraction(
            faqListContainer,
            findViewById(R.id.faq3Container),
            findViewById(R.id.faq3Header),
            findViewById(R.id.tvAnswer3),
            findViewById(R.id.ivArrow3),
            isExpandedInitially = false
        )

        setupFaqInteraction(
            faqListContainer,
            findViewById(R.id.faq4Container),
            findViewById(R.id.faq4Header),
            findViewById(R.id.tvAnswer4),
            findViewById(R.id.ivArrow4),
            isExpandedInitially = false
        )
    }

    /**
     * Fungsi utama untuk membuat Accordion Interaktif (Expand/Collapse)
     */
    private fun setupFaqInteraction(
        parentContainer: LinearLayout, // Container utama seluruh list untuk animasi smooth
        itemContainer: LinearLayout,   // Container background masing-masing FAQ
        header: RelativeLayout,        // Area klik
        answerText: TextView,          // Teks jawaban (yang di-hide/show)
        arrowIcon: ImageView,          // Ikon panah (untuk rotasi)
        isExpandedInitially: Boolean
    ) {
        var isExpanded = isExpandedInitially

        header.setOnClickListener {
            // Minta Android membuat transisi otomatis yang halus
            TransitionManager.beginDelayedTransition(parentContainer, AutoTransition().apply {
                duration = 200 // Kecepatan buka/tutup (ms)
            })

            // Balik state
            isExpanded = !isExpanded

            if (isExpanded) {
                // Saat DIBUKA
                answerText.visibility = View.VISIBLE
                arrowIcon.animate().rotation(180f).setDuration(200).start()
                arrowIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary_blue)) // Panah Biru
                itemContainer.setBackgroundResource(R.drawable.bg_faq_expanded) // Background Biru Soft
                // Ubah judul jadi hitam pekat
                (header.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            } else {
                // Saat DITUTUP
                answerText.visibility = View.GONE
                arrowIcon.animate().rotation(0f).setDuration(200).start()
                arrowIcon.setColorFilter(ContextCompat.getColor(this, R.color.slate_400)) // Panah Abu-abu
                itemContainer.setBackgroundResource(R.drawable.bg_faq_collapsed) // Background Putih
                // Ubah judul jadi abu tua
                (header.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(this, R.color.slate_500))
            }
        }
    }
}