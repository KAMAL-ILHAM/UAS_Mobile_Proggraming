package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class LupaPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflate layout untuk fragment ini
        val view = inflater.inflate(R.layout.fragment_lupa_password, container, false)

        // 2. Kenalkan komponen UI
        val btnBack: ImageView = view.findViewById(R.id.btnBackForgot)
        val btnKirim: MaterialButton = view.findViewById(R.id.btnKirimReset)

        // 3. Fungsi tombol kembali (Menutup fragment)
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // 4. Fungsi tombol kirim tautan
        btnKirim.setOnClickListener {
            Toast.makeText(context, "Tautan reset telah dikirim ke email!", Toast.LENGTH_LONG).show()
        }

        return view
    }
}