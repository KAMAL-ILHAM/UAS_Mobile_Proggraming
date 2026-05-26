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
        // Menyambungkan dengan desain XML fragment_onboarding_dark yang sudah kamu buat
        val view = inflater.inflate(R.layout.activity_fragment_onboarding_dark, container, false)

        // Mengambil ID area tombol "GESER UNTUK MULAI"
        // ... di dalam onCreateView OnboardingDarkFragment.kt ...
        val btnStart = view.findViewById<LinearLayout>(R.id.btnStartWrapper) // Asumsi ID tombol di XML

        btnStart.setOnClickListener {
            // Pindah ke LoginActivity
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Tutup onboarding agar tidak bisa kembali
        }

        return view
    }
}