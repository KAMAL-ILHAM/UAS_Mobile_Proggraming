package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
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
        fun newInstance(title: String, desc: String, imageResId: Int, position: Int): OnboardingLightFragment {
            val fragment = OnboardingLightFragment()
            val args = Bundle()
            args.putString("TITLE", title)
            args.putString("DESC", desc)
            args.putInt("IMAGE", imageResId)
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
        val ivOnboarding = view.findViewById<ImageView>(R.id.ivOnboarding)
        val btnNext = view.findViewById<FrameLayout>(R.id.btnNextOnboarding)

        tvTitle.text = arguments?.getString("TITLE")
        tvDesc.text = arguments?.getString("DESC")

        val imageRes = arguments?.getInt("IMAGE") ?: R.drawable.bg_splash_logo
        ivOnboarding.setImageResource(imageRes)

        val position = arguments?.getInt("POSITION") ?: 0
        setupDots(view, position)

        // --- PERBAIKAN LOGIKA TOMBOL NEXT ---
        btnNext.setOnClickListener {
            if (position < 2) {
                // Jika masih di slide 1 atau 2, suruh Activity geser layar perlahan
                (requireActivity() as OnboardingActivity).moveToNextSlide()
            } else {
                // Jika ini adalah slide 3 (terakhir), langsung pindah ke Login
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)

                // Menutup OnboardingActivity agar tidak bisa di-back
                requireActivity().finish()
            }
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

        // --- PERBAIKAN: Menambahkan kondisi untuk slide ke-3 ---
        when (position) {
            0 -> {
                dot1.layoutParams = activeParams
                dot1.setBackgroundResource(R.drawable.bg_dot_active)
            }
            1 -> {
                dot2.layoutParams = activeParams
                dot2.setBackgroundResource(R.drawable.bg_dot_active)
            }
            2 -> {
                dot3.layoutParams = activeParams
                dot3.setBackgroundResource(R.drawable.bg_dot_active)
            }
        }
    }
}