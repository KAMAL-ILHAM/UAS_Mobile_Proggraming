package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton

class BuatLaporanLangkah2Activity : AppCompatActivity() {

    private var incomingKategori: String = ""
    private var incomingGedung: String = ""
    private var incomingRuangan: String = ""
    private var imageUriString: String = ""

    private lateinit var etDetailMasalah: EditText
    private lateinit var tvCounter: TextView
    private lateinit var btnLanjut: MaterialButton
    private lateinit var cardUploadFoto: CardView
    private lateinit var ivPreviewFoto: ImageView
    private lateinit var layoutPlaceholderFoto: LinearLayout

    private var isPhotoUploaded = false
    private var isTextValid = false

    // Launcher Kamera Modern Android
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Tampilkan foto
            ivPreviewFoto.setImageBitmap(bitmap)
            ivPreviewFoto.visibility = View.VISIBLE
            layoutPlaceholderFoto.visibility = View.GONE
            isPhotoUploaded = true

            val tempUri = saveBitmapToCache(bitmap)
            imageUriString = tempUri.toString() // Simpan alamat file-nya

            validateForm()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_langkah2)
        incomingKategori = intent.getStringExtra("KATEGORI_TERPILIH") ?: ""
        incomingGedung = intent.getStringExtra("GEDUNG_TERPILIH") ?: ""
        incomingRuangan = intent.getStringExtra("RUANGAN_TERPILIH") ?: ""

        // 1. Kenalkan Views
        etDetailMasalah = findViewById(R.id.etDetailMasalah)
        tvCounter = findViewById(R.id.tvCounter)
        btnLanjut = findViewById(R.id.btnLanjut)
        cardUploadFoto = findViewById(R.id.cardUploadFoto)
        ivPreviewFoto = findViewById(R.id.ivPreviewFoto)
        layoutPlaceholderFoto = findViewById(R.id.layoutPlaceholderFoto)

        // 2. Tombol Kembali
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 3. Counter Text Listener
        etDetailMasalah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                tvCounter.text = "$currentLength/300"

                isTextValid = currentLength > 5 // Minimal 5 huruf
                validateForm()
            }
        })

        // 4. Klik Buka Kamera
        cardUploadFoto.setOnClickListener {
            takePictureLauncher.launch(null) // Langsung buka kamera
        }

        // 5. Klik Tombol Lanjut
        // 5. Klik Tombol Lanjut
        btnLanjut.setOnClickListener {
            val detailMasalah = etDetailMasalah.text.toString().trim()

            val intent = Intent(this, BuatLaporanKonfirmasiActivity::class.java)

            intent.putExtra("KATEGORI_FINAL", incomingKategori)
            intent.putExtra("GEDUNG_FINAL", incomingGedung)
            intent.putExtra("RUANGAN_FINAL", incomingRuangan)
            intent.putExtra("DETAIL_FINAL", detailMasalah)
            intent.putExtra("FOTO_URI", imageUriString)

            startActivity(intent)
        }
    }

    // Fungsi Validasi Tombol (Berubah Biru jika lengkap)
    private fun validateForm() {
        if (isTextValid && isPhotoUploaded) {
            // Aktif (Biru)
            btnLanjut.isEnabled = true
            btnLanjut.setBackgroundColor(Color.parseColor("#2563EB"))
            btnLanjut.setTextColor(Color.WHITE)
            btnLanjut.setIconTintResource(R.color.white) // Pastikan R.color.white ada di colors.xml
            btnLanjut.elevation = 8f
        } else {
            // Tidak Aktif (Abu-abu)
            btnLanjut.isEnabled = false
            btnLanjut.setBackgroundColor(Color.parseColor("#F1F5F9"))
            btnLanjut.setTextColor(Color.parseColor("#94A3B8"))
            // Jika ingin set tint warna abu secara programatik
            // btnLanjut.iconTint = ColorStateList.valueOf(Color.parseColor("#94A3B8"))
            btnLanjut.elevation = 0f
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): android.net.Uri {
        // Buat file fisik bernama bukti_laporan_xxx.jpg di folder cache
        val file = java.io.File(cacheDir, "bukti_laporan_${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        // Kembalikan alamat (URI) dari file fisik tersebut
        return android.net.Uri.fromFile(file)
    }
}