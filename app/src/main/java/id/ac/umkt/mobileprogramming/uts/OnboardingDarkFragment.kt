package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class OnboardingDarkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_fragment_onboarding_dark, container, false)

        // Mengambil ID area tombol berdasarkan XML
        val btnStart = view.findViewById<LinearLayout>(R.id.btnStartWrapper)

        btnStart.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)

            // Menggunakan animasi naik ke atas yang baru dibuat
            requireActivity().overridePendingTransition(
                R.anim.premium_slide_scale_in,
                R.anim.premium_fade_scale_out
            )
            requireActivity().finish() // Tutup onboarding agar tidak bisa di-back
        }

        return view
    }
}