package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class BuatSandiBaruFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buat_sandi_baru, container, false)

        val btnBack: ImageView = view.findViewById(R.id.btnBackReset)
        val btnSave: MaterialButton = view.findViewById(R.id.btnSavePassword)

        // SUDAH DICOCOKKAN DENGAN ID DI XML
        val etSandiBaru = view.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etKonfirmasiSandi = view.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            val sandiBaru = etSandiBaru?.text.toString().trim()
            val konfirmasi = etKonfirmasiSandi?.text.toString().trim()

            // 1. Validasi Input Kosong
            if (sandiBaru.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(requireContext(), "Harap isi kedua kolom sandi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Validasi Keamanan Sandi (Disamakan dengan Hint XML: 8 Karakter)
            if (sandiBaru.length < 8) {
                Toast.makeText(requireContext(), "Sandi minimal 8 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Validasi Kecocokan
            if (sandiBaru != konfirmasi) {
                Toast.makeText(requireContext(), "Konfirmasi sandi tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

            // Tangkap kode rahasia dari email (jika ada)
            val oobCode = arguments?.getString("OOB_CODE")

            if (oobCode != null) {
                // =========================================================
                // SKENARIO 1: RESET SANDI DARI LINK EMAIL (TANPA LOGIN)
                // =========================================================
                btnSave.isEnabled = false
                btnSave.text = "Memverifikasi..."

                // Konfirmasi ke Firebase menggunakan oobCode
                auth.confirmPasswordReset(oobCode, sandiBaru).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Sandi berhasil direset! Silakan login.", Toast.LENGTH_LONG).show()
                        requireActivity().supportFragmentManager.popBackStack() // Kembali ke Login
                    } else {
                        btnSave.isEnabled = true
                        btnSave.text = "Simpan Sandi Baru"
                        Toast.makeText(requireContext(), "Gagal: Link sudah kadaluarsa atau salah.", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (user != null) {
                // =========================================================
                // SKENARIO 2: AKSES DARI MENU PROFIL (USER SEDANG LOGIN)
                // =========================================================
                btnSave.isEnabled = false
                btnSave.text = "Menyimpan..."

                user.updatePassword(sandiBaru).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Sandi berhasil diubah! Silakan login kembali.", Toast.LENGTH_LONG).show()

                        auth.signOut()
                        val sharedPref = requireActivity().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
                        sharedPref.edit().clear().apply()

                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        btnSave.isEnabled = true
                        btnSave.text = "Simpan Sandi Baru"
                        Toast.makeText(requireContext(), "Gagal mengubah sandi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }
}