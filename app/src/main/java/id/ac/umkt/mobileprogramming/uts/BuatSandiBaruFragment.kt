package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class BuatSandiBaruFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buat_sandi_baru, container, false)

        // Inisialisasi View (Pastikan ID di XML sesuai dengan ini)
        val btnBack: ImageView = view.findViewById(R.id.btnBackReset)
        val btnSave: MaterialButton = view.findViewById(R.id.btnSavePassword)

        // 1. Fungsi tombol panah kembali
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // 2. Fungsi tombol simpan (Simulasi)
        btnSave.setOnClickListener {
            // Munculkan notifikasi sukses
            Toast.makeText(requireContext(), "Kata sandi berhasil diperbarui! Silakan login kembali.", Toast.LENGTH_LONG).show()

            // Kembali ke halaman Login dan hapus jejak Deep Link
            val intent = Intent(requireContext(), LoginActivity::class.java)

            // Flags ini sangat penting agar pengguna tidak bisa kembali ke halaman ganti sandi
            // saat menekan tombol back di HP setelah berhasil ganti sandi.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}