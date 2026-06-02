package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import java.io.File

class BuatLaporanLangkah2Activity : AppCompatActivity() {

    private var incomingKategori: String = ""
    private var incomingGedung: String = ""
    private var incomingRuangan: String = ""
    private var imageUriString: String = ""

    // Deklarasi variabel untuk menampung Uri foto resolusi tinggi
    private var currentPhotoUri: Uri? = null

    private lateinit var etDetailMasalah: EditText
    private lateinit var tvCounter: TextView
    private lateinit var btnLanjut: MaterialButton
    private lateinit var cardUploadFoto: CardView
    private lateinit var ivPreviewFoto: ImageView
    private lateinit var layoutPlaceholderFoto: LinearLayout

    private var isPhotoUploaded = false
    private var isTextValid = false

    // 1. Launcher Kamera (Full Resolution)
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            currentPhotoUri?.let { uri ->
                ivPreviewFoto.setImageURI(uri)
                ivPreviewFoto.visibility = View.VISIBLE
                layoutPlaceholderFoto.visibility = View.GONE
                isPhotoUploaded = true
                imageUriString = uri.toString()
                validateForm()
            }
        }
    }

    // 2. Launcher Galeri (Hanya memfilter format gambar)
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            ivPreviewFoto.setImageURI(uri)
            ivPreviewFoto.visibility = View.VISIBLE
            layoutPlaceholderFoto.visibility = View.GONE
            isPhotoUploaded = true
            imageUriString = uri.toString()
            validateForm()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan_langkah2)

        incomingKategori = intent.getStringExtra("KATEGORI_TERPILIH") ?: ""
        incomingGedung = intent.getStringExtra("GEDUNG_TERPILIH") ?: ""
        incomingRuangan = intent.getStringExtra("RUANGAN_TERPILIH") ?: ""

        // 1. Kenalkan Views (ID tidak ada yang diubah)
        etDetailMasalah = findViewById(R.id.etDetailMasalah)
        tvCounter = findViewById(R.id.tvCounter)
        btnLanjut = findViewById(R.id.btnLanjut)
        cardUploadFoto = findViewById(R.id.cardUploadFoto)
        ivPreviewFoto = findViewById(R.id.ivPreviewFoto)
        layoutPlaceholderFoto = findViewById(R.id.layoutPlaceholderFoto)

        // Letakkan di dalam onCreate BuatLaporanLangkah2Activity
        val tvLabelDetail = findViewById<TextView>(R.id.tvLabelDetail) // Sesuaikan ID
        val tvLabelFoto = findViewById<TextView>(R.id.tvLabelFoto) // Sesuaikan ID

        val htmlDetail = "DETAIL MASALAH <font color='#EF4444'>*</font> 0/300"
        val htmlFoto = "BUKTI FOTO <font color='#EF4444'>*</font>"

        tvLabelDetail?.text = androidx.core.text.HtmlCompat.fromHtml(htmlDetail, androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvLabelFoto?.text = androidx.core.text.HtmlCompat.fromHtml(htmlFoto, androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY)

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

        // 4. Klik Buka Kamera / Galeri (Memunculkan Dialog)
        cardUploadFoto.setOnClickListener {
            showImageSourceDialog()
        }

        // 5. Klik Tombol Lanjut
        btnLanjut.setOnClickListener {

            if (!isPhotoUploaded) {
                android.widget.Toast.makeText(this, "Harap lampirkan bukti foto terlebih dahulu!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isTextValid) {
                android.widget.Toast.makeText(this, "Detail masalah minimal 5 karakter!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val detailMasalah = etDetailMasalah.text.toString().trim()
            val intent = Intent(this, BuatLaporanKonfirmasiActivity::class.java)

            intent.putExtra("KATEGORI_FINAL", incomingKategori)
            intent.putExtra("GEDUNG_FINAL", incomingGedung)
            intent.putExtra("RUANGAN_FINAL", incomingRuangan)
            intent.putExtra("DETAIL_FINAL", detailMasalah)
            intent.putExtra("FOTO_URI", imageUriString)

            startActivity(intent)
        }

        validateForm()
    }

    // Fungsi untuk membuat file kosong sementara sebelum kamera dibuka
    private fun createImageUri(): Uri {
        val file = File(cacheDir, "bukti_laporan_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    }

    // Fungsi Dialog Pilihan Sumber Foto
    private fun showImageSourceDialog() {
        val options = arrayOf("Ambil dari Kamera", "Pilih dari Galeri")
        android.app.AlertDialog.Builder(this)
            .setTitle("Lampirkan Bukti Foto")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Buka Kamera
                        currentPhotoUri = createImageUri()
                        currentPhotoUri?.let { takePictureLauncher.launch(it) }
                    }
                    1 -> {
                        // Buka Galeri
                        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            }
            .show()
    }

    // Fungsi Validasi Tombol (Berubah Biru jika lengkap)
    private fun validateForm() {
        if (isTextValid && isPhotoUploaded) {
            // Aktif (Biru)
            btnLanjut.isEnabled = true
            btnLanjut.setBackgroundColor(Color.parseColor("#2563EB"))
            btnLanjut.setTextColor(Color.WHITE)
            btnLanjut.setIconTintResource(R.color.white)
            btnLanjut.elevation = 8f
        } else {
            // Tidak Aktif (Abu-abu)
            btnLanjut.isEnabled = false
            btnLanjut.setBackgroundColor(Color.parseColor("#F1F5F9"))
            btnLanjut.setTextColor(Color.parseColor("#94A3B8"))
            btnLanjut.elevation = 0f
        }
    }
}