package id.ac.umkt.mobileprogramming.uts

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
        // Ganti 'nama_file_xml_kamu' dengan nama file layout XML yang sedang kamu buka ini
        // Misalnya: R.layout.fragment_buat_sandi_baru
        val view = inflater.inflate(R.layout.fragment_buat_sandi_baru, container, false)

        // Mengenalkan tombol dari XML ke Kotlin
        val btnBack: ImageView = view.findViewById(R.id.btnBackReset)
        val btnSave: MaterialButton = view.findViewById(R.id.btnSavePassword)

        // Fungsi tombol panah kembali
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Fungsi tombol simpan
        btnSave.setOnClickListener {
            Toast.makeText(context, "Kata sandi berhasil diperbarui!", Toast.LENGTH_SHORT).show()

            // Setelah berhasil ganti sandi, tutup semua fragment dan kembali ke layar Login utama
            requireActivity().supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return view
    }
}