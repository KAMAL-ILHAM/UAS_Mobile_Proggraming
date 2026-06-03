package id.ac.umkt.mobileprogramming.uts

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class DetailRiwayatActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var documentId: String = ""
    private var nomorTiket: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_riwayat)

        db = FirebaseFirestore.getInstance()

        documentId = intent.getStringExtra("DOCUMENT_ID") ?: ""
        nomorTiket = intent.getStringExtra("NOMOR_TIKET") ?: ""

        findViewById<FrameLayout>(R.id.btnBackDetail)?.setOnClickListener { finish() }

        fetchDetailLaporan()
    }

    private fun fetchDetailLaporan() {
        if (documentId.isEmpty()) {
            Toast.makeText(this, "ID Laporan tidak valid atau hilang.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("laporan").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    val dbKategori = document.getString("kategori") ?: "Fasilitas"
                    val dbLokasi = document.getString("lokasi") ?: "Lokasi Tidak Diketahui"
                    val dbStatus = document.getString("status") ?: "SEDANG DIPROSES"
                    val dbDetail = document.getString("detail") ?: "Tidak ada deskripsi."
                    val dbNomorTiket = document.getString("nomor_tiket") ?: nomorTiket

                    val date = document.getTimestamp("timestamp")?.toDate()
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                    val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

                    val dateString = if (date != null) dateFormat.format(date) else "-"
                    val dateTimeString = if (date != null) "${dateTimeFormat.format(date)} WITA" else "-"

                    findViewById<TextView>(R.id.tvReportId)?.text = "ID: $dbNomorTiket"
                    findViewById<TextView>(R.id.tvDetailTitle)?.text = dbKategori
                    findViewById<TextView>(R.id.tvDetailTanggal)?.text = dateString
                    findViewById<TextView>(R.id.tvDetailLokasi)?.text = dbLokasi
                    findViewById<TextView>(R.id.tvDetailDeskripsi)?.text = "\"$dbDetail\""

                    val tvStatus = findViewById<TextView>(R.id.badgeStatusDetail)
                    val cleanStatus = dbStatus.replace("● ", "").uppercase()
                    tvStatus?.text = cleanStatus

                    val bgDrawable = GradientDrawable().apply { cornerRadius = 12f }

                    // Elemen Jejak Penyelesaian
                    val bgIconTimeline1 = findViewById<FrameLayout>(R.id.bgIconTimeline1)
                    val ivIconTimeline1 = findViewById<ImageView>(R.id.ivIconTimeline1)
                    val tvTitleTimeline1 = findViewById<TextView>(R.id.tvTitleTimeline1)
                    val tvDescTimeline1 = findViewById<TextView>(R.id.tvDescTimeline1)
                    val tvDateTimeline1 = findViewById<TextView>(R.id.tvDateTimeline1)
                    val tvDateTimeline2 = findViewById<TextView>(R.id.tvDateTimeline2)

                    tvDateTimeline2?.text = dateTimeString

                    // Logika Dinamis Status & Jejak Penyelesaian
                    when {
                        cleanStatus.contains("SELESAI") -> {
                            tvStatus?.setTextColor(Color.parseColor("#10B981"))
                            bgDrawable.setColor(Color.parseColor("#E6F9F3"))

                            bgIconTimeline1?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
                            ivIconTimeline1?.setImageResource(R.drawable.ic_check_timeline)
                            tvTitleTimeline1?.text = "Perbaikan Selesai"
                            tvDescTimeline1?.text = "Perbaikan telah selesai dilakukan. Fasilitas berfungsi normal."
                            tvDateTimeline1?.text = dateTimeString
                            tvDateTimeline1?.setTextColor(Color.parseColor("#10B981"))
                        }
                        cleanStatus.contains("BATAL") || cleanStatus.contains("TOLAK") -> {
                            tvStatus?.setTextColor(Color.parseColor("#EF4444"))
                            bgDrawable.setColor(Color.parseColor("#FEE2E2"))

                            bgIconTimeline1?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EF4444"))
                            ivIconTimeline1?.setImageResource(R.drawable.ic_cancel)
                            tvTitleTimeline1?.text = "Laporan Dibatalkan"
                            tvDescTimeline1?.text = "Laporan ini dibatalkan atau ditolak oleh sistem/admin."
                            tvDateTimeline1?.text = dateTimeString
                            tvDateTimeline1?.setTextColor(Color.parseColor("#EF4444"))
                        }
                        else -> {
                            tvStatus?.setTextColor(Color.parseColor("#10B981"))
                            bgDrawable.setColor(Color.parseColor("#E6F9F3"))

                            bgIconTimeline1?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FBBC05"))
                            ivIconTimeline1?.setImageResource(R.drawable.ic_technician2)
                            tvTitleTimeline1?.text = "Sedang Diproses"
                            tvDescTimeline1?.text = "Laporan sedang dalam antrean atau sedang ditinjau oleh teknisi."
                            tvDateTimeline1?.text = "Menunggu pembaruan..."
                            tvDateTimeline1?.setTextColor(Color.parseColor("#FBBC05"))
                        }
                    }
                    tvStatus?.background = bgDrawable

                    // 🔥 LOGIKA BARU: TAMPILKAN FOTO BUKTI DARI BASE64
                    val ivBigIcon = findViewById<ImageView>(R.id.ivBigIcon)
                    val dbFotoBase64 = document.getString("foto_base64") ?: ""

                    if (dbFotoBase64.isNotEmpty()) {
                        try {
                            val imageBytes = android.util.Base64.decode(dbFotoBase64, android.util.Base64.DEFAULT)
                            val decodedImage = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            ivBigIcon?.setImageBitmap(decodedImage)
                            ivBigIcon?.clearColorFilter()
                            ivBigIcon?.scaleType = ImageView.ScaleType.CENTER_CROP
                            ivBigIcon?.alpha = 0.5f
                        } catch (e: Exception) {
                            e.printStackTrace()
                            setFallbackIcon(ivBigIcon, dbKategori)
                        }
                    } else {
                        setFallbackIcon(ivBigIcon, dbKategori)
                    }

                } else {
                    Toast.makeText(this, "Data laporan tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal sinkronisasi data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setFallbackIcon(imageView: ImageView?, kategori: String) {
        val iconRes = when (kategori.lowercase(Locale.ROOT)) {
            "elektronik" -> R.drawable.ic_monitor
            "furnitur" -> R.drawable.ic_furnitur
            "sanitasi" -> R.drawable.ic_sanitasi
            "jaringan" -> R.drawable.ic_jaringan
            "gedung" -> R.drawable.ic_gedung
            "area luar" -> R.drawable.ic_outdoor
            else -> R.drawable.ic_document
        }
        imageView?.setImageResource(iconRes)
        imageView?.alpha = 0.15f
        imageView?.setColorFilter(Color.WHITE)
        imageView?.scaleType = ImageView.ScaleType.FIT_CENTER
    }
}