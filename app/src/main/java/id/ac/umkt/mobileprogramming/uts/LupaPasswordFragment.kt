package id.ac.umkt.mobileprogramming.uts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LupaPasswordFragment : Fragment(R.layout.fragment_lupa_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // PERBAIKAN 1

        // PERBAIKAN 2: Menggunakan ID yang benar dari file XML
        val etEmail = view.findViewById<EditText>(R.id.etForgotEmail)
        val btnKirim = view.findViewById<Button>(R.id.btnKirimReset)

        // ... lanjutkan dengan sisa kode tombol klik kamu di bawah sini ...

        btnKirim.setOnClickListener {
            val emailTujuan = etEmail.text.toString().trim()

            if (emailTujuan.isEmpty()) {
                Toast.makeText(requireContext(), "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnKirim.isEnabled = false
            btnKirim.text = "Mengirim..."

            // 1. Persiapkan Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://sandbox.api.mailtrap.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(MailtrapApi::class.java)

            // 2. Desain Isi Email & Tombol Deep Link (laporyuk://reset_sandi)
            val htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; text-align: center;">
                    <h2>Reset Sandi Fix It</h2>
                    <p>Kami menerima permintaan untuk mereset sandi akun Anda.</p>
                    <br>
                    <a href="laporyuk://reset_sandi" style="padding: 12px 24px; background-color: #2563EB; color: white; text-decoration: none; border-radius: 8px; font-weight: bold;">Reset Sandi Sekarang</a>
                    <br><br>
                    <p style="color: #64748B; font-size: 12px;">Abaikan pesan ini jika Anda tidak merasa meminta reset sandi.</p>
                </div>
            """.trimIndent()

            val requestData = MailtrapRequest(
                to = listOf(EmailAddress(email = emailTujuan, name = "Pengguna EIO")),
                from = EmailAddress(email = "admin@fixit.com", name = "Admin Fix It"),
                subject = "Permintaan Reset Sandi - Fix It",
                html = htmlContent
            )

            // ⚠️ GANTI DUA TEKS DI BAWAH INI DENGAN DATA DARI AKUN MAILTRAP KAMU
            val inboxIdKamu = "4613070"
            val apiTokenKamu = "b0b01acd38668ae0d8a0cd5f874b809d"

            // 3. Eksekusi Pengiriman Email
            api.sendEmail(inboxIdKamu, apiTokenKamu, requestData).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    btnKirim.isEnabled = true
                    btnKirim.text = "Kirim Link Reset"

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Cek kotak masuk Mailtrap kamu!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal ngirim! Cek Token/Inbox ID.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    btnKirim.isEnabled = true
                    btnKirim.text = "Kirim Link Reset"
                    Toast.makeText(requireContext(), "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}