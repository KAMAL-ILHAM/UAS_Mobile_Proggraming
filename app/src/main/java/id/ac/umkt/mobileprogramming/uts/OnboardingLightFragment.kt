package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class OnboardingLightFragment : Fragment() {

    companion object {
        // Tambahkan parameter imageResId
        fun newInstance(title: String, desc: String, imageResId: Int, position: Int): OnboardingLightFragment {
            val fragment = OnboardingLightFragment()
            val args = Bundle()
            args.putString("TITLE", title)
            args.putString("DESC", desc)
            args.putInt("IMAGE", imageResId) // Simpan gambar
            args.putInt("POSITION", position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_fragment_onboarding_light, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitleOnboarding)
        val tvDesc = view.findViewById<TextView>(R.id.tvDescOnboarding)
        val ivOnboarding = view.findViewById<ImageView>(R.id.ivOnboarding) // Ambil ImageView
        val btnNext = view.findViewById<FrameLayout>(R.id.btnNextOnboarding)

        // Set Judul, Deskripsi, dan GAMBAR
        tvTitle.text = arguments?.getString("TITLE")
        tvDesc.text = arguments?.getString("DESC")

        // Pasang gambar sesuai slide
        val imageRes = arguments?.getInt("IMAGE") ?: R.drawable.bg_splash_logo
        ivOnboarding.setImageResource(imageRes)

        // Set Titik Indikator
        val position = arguments?.getInt("POSITION") ?: 0
        setupDots(view, position)

        // Saat tombol diklik, suruh Activity geser ke slide berikutnya
        btnNext.setOnClickListener {
            (requireActivity() as OnboardingActivity).moveToNextSlide()
        }

        return view
    }

    private fun setupDots(view: View, position: Int) {
        val dot1 = view.findViewById<View>(R.id.dot1)
        val dot2 = view.findViewById<View>(R.id.dot2)
        val dot3 = view.findViewById<View>(R.id.dot3)

        fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

        val inactiveParams = LinearLayout.LayoutParams(dpToPx(8), dpToPx(8)).apply { setMargins(dpToPx(4), 0, dpToPx(4), 0) }
        val activeParams = LinearLayout.LayoutParams(dpToPx(24), dpToPx(6)).apply { setMargins(dpToPx(4), 0, dpToPx(4), 0) }

        dot1.layoutParams = inactiveParams
        dot1.setBackgroundResource(R.drawable.bg_dot_inactive)
        dot2.layoutParams = inactiveParams
        dot2.setBackgroundResource(R.drawable.bg_dot_inactive)
        dot3.layoutParams = inactiveParams
        dot3.setBackgroundResource(R.drawable.bg_dot_inactive)

        when (position) {
            0 -> {
                dot1.layoutParams = activeParams
                dot1.setBackgroundResource(R.drawable.bg_dot_active)
            }
            1 -> {
                dot2.layoutParams = activeParams
                dot2.setBackgroundResource(R.drawable.bg_dot_active)
            }
        }
    }
}