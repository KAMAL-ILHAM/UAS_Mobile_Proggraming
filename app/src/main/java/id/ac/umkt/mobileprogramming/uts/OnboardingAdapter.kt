package id.ac.umkt.mobileprogramming.uts

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Menentukan total halaman onboarding (3 halaman)
    override fun getItemCount(): Int = 3

    // Mengatur fragment dan data apa yang muncul di setiap halaman
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                // Slide 1: Tema Terang (Ikon Toga Biru)
                OnboardingLightFragment.newInstance(
                    "Sistem Pendamping\nBelajar & Fasilitas",
                    "Akses mudah untuk kebutuhan akademik dan layanan pelaporan kerusakan infrastruktur kampus dalam satu genggaman.",
                    R.drawable.onboarding_belajar, // Ikon biru
                    0 // Posisi halaman ke-1 (index 0)
                )
            }
            1 -> {
                // Slide 2: Tema Terang (Ikon Kunci Pas Kuning)
                OnboardingLightFragment.newInstance(
                    "Pantau Perbaikan\nSecara Real-time",
                    "Laporkan fasilitas kampus yang rusak, lacak progres penanganannya, dan komunikasikan langsung dengan teknisi.",
                    R.drawable.onboarding_perbaikan, // Ikon kuning
                    1 // Posisi halaman ke-2 (index 1)
                )
            }
            2 -> {
                // Slide 3: Tema Gelap (Ikon Perisai)
                OnboardingDarkFragment()
            }
            else -> {
                // Fallback default jika terjadi error indeks
                OnboardingDarkFragment()
            }
        }
    }
}