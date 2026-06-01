package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class LupaPasswordFragment : Fragment(R.layout.fragment_lupa_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Kenalkan Views
        val etEmail = view.findViewById<EditText>(R.id.etForgotEmail)
        val btnKirim = view.findViewById<Button>(R.id.btnKirimReset)
        // Tombol kembali (jika ada di XML Lupa Password, biasanya ID-nya btnBack)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        // 2. Fungsi Tombol Kembali
        btnBack?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // 3. Fungsi Tombol Kirim Email Reset (LANGSUNG VIA FIREBASE)
        btnKirim.setOnClickListener {
            val emailTujuan = etEmail.text.toString().trim()

            if (emailTujuan.isEmpty()) {
                Toast.makeText(requireContext(), "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ubah state tombol
            btnKirim.isEnabled = false
            btnKirim.text = "Mengirim..."

            // Eksekusi fungsi bawaan Firebase
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailTujuan)
                .addOnCompleteListener { task ->
                    // Kembalikan state tombol
                    btnKirim.isEnabled = true
                    btnKirim.text = "Kirim Link Reset"

                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Link reset sandi telah dikirim ke $emailTujuan. Silakan cek kotak masuk atau folder spam Anda.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Tutup halaman lupa password dan kembali ke halaman Login
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal mengirim email: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}