package id.ac.umkt.mobileprogramming.uts

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class BuatLaporanActivity : AppCompatActivity() {

    // Simpan data kategori tambahan untuk bottom sheet
    private val subKategoriList = listOf(
        CategoryItem("Furnitur", R.drawable.ic_furnitur4),
        CategoryItem("Elektronik", R.drawable.ic_electronic3),
        CategoryItem("Sanitasi", R.drawable.ic_sanitasi3),
        CategoryItem("Jaringan", R.drawable.ic_jaringan3),
        CategoryItem("Gedung", R.drawable.ic_gedung3),
        CategoryItem("Area Luar", R.drawable.ic_outdoors3)
    )

    private lateinit var cardsList: List<LinearLayout>
    private var selectedGedungPosition = 4 // Default Gedung E (urutan ke-4 indeks array)
    private var selectedKategori: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan)

        // Binding Views Utama
        val btnClose = findViewById<ImageView>(R.id.btnClose)
        val cardFurnitur = findViewById<LinearLayout>(R.id.cardFurnitur)
        val cardElektronik = findViewById<LinearLayout>(R.id.cardElektronik)
        val cardSanitasi = findViewById<LinearLayout>(R.id.cardSanitasi)
        val cardLainnya = findViewById<LinearLayout>(R.id.cardLainnya)
        val dropdownGedung = findViewById<LinearLayout>(R.id.dropdownGedung)
        val tvSelectedGedung = findViewById<TextView>(R.id.tvSelectedGedung)
        val ivChevron = dropdownGedung.getChildAt(2) as ImageView // Arrow kanan/bawah dropdown
        val etNamaRuangan = findViewById<EditText>(R.id.etNamaRuangan)
        val btnLanjut = findViewById<MaterialButton>(R.id.btnLanjut)

        cardsList = listOf(cardFurnitur, cardElektronik, cardSanitasi, cardLainnya)

        // Tombol Close (Kembali)
        btnClose.setOnClickListener { finish() }

        // 1. Logika Klik Kategori Utama
        cardFurnitur.setOnClickListener { changeActiveCategoryState(it as LinearLayout, "Furnitur", R.drawable.ic_furnitur3) }
        cardElektronik.setOnClickListener { changeActiveCategoryState(it as LinearLayout, "Elektronik", R.drawable.ic_electronic3) }
        cardSanitasi.setOnClickListener { changeActiveCategoryState(it as LinearLayout, "Sanitasi", R.drawable.ic_sanitasi3) }

        // 2. Logika Klik "Lainnya" Membuka Bottom Sheet
        cardLainnya.setOnClickListener {
            showSubCategoryBottomSheet { selectedCat ->
                // Ketika sub kategori dipilih, set cardLainnya menjadi active state secara visual
                changeActiveCategoryState(cardLainnya, selectedCat.title, selectedCat.iconRes)
            }
        }

        // 3. Logika Dropdown Gedung Kustom
        val daftarGedung = arrayOf("Gedung A", "Gedung B", "Gedung C", "Gedung D", "Gedung E", "Gedung F", "Gedung G")
        val popupWindow = ListPopupWindow(this)
        val adapterDropdown = DropdownGedungAdapter(this, daftarGedung)

        popupWindow.setAdapter(adapterDropdown)
        popupWindow.anchorView = dropdownGedung
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.setWidth(dropdownGedung.width)
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dropdown_menu))
        popupWindow.isModal = true

        dropdownGedung.setOnClickListener {
            // Animasi arrow berputar ke atas saat dibuka
            ivChevron.animate().rotation(180f).setDuration(250).start()
            popupWindow.setWidth(dropdownGedung.width) // Set lebar dinamis menyesuaikan layar device
            popupWindow.show()
            popupWindow.listView?.choiceMode = android.widget.AbsListView.CHOICE_MODE_SINGLE
            popupWindow.listView?.setItemChecked(selectedGedungPosition, true)
        }

        popupWindow.setOnItemClickListener { _, _, position, _ ->
            selectedGedungPosition = position
            tvSelectedGedung.text = daftarGedung[position]
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            // Animasi arrow kembali berputar ke bawah saat ditutup
            ivChevron.animate().rotation(0f).setDuration(250).start()
        }

        // 4. Logika Tombol Lanjut ke Langkah 2
        btnLanjut.setOnClickListener {
            val namaRuangan = etNamaRuangan.text.toString().trim()

            if (selectedKategori == null) {
                Toast.makeText(this, "Harap pilih kategori fasilitas terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Premium: Cegah lanjut jika nama ruangan belum diisi
            if (namaRuangan.isEmpty()) {
                Toast.makeText(this, "Harap isi nama ruangan terlebih dahulu", Toast.LENGTH_SHORT).show()
                etNamaRuangan.requestFocus() // Arahkan fokus ke kolom yang kosong
                return@setOnClickListener
            }

            // Pindah ke Halaman Langkah 2
            val intent = Intent(this, BuatLaporanLangkah2Activity::class.java)

            // Membawa data pilihan ke halaman selanjutnya (opsional)
            intent.putExtra("KATEGORI_TERPILIH", selectedKategori)
            intent.putExtra("GEDUNG_TERPILIH", tvSelectedGedung.text.toString())
            intent.putExtra("RUANGAN_TERPILIH", namaRuangan)

            startActivity(intent)

            // Animasi slide transisi modern bawaan Android
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    /**
     * Mengatur state visual kartu kategori aktif secara dinamis (Pixel-Perfect State Handling)
     */
    private fun changeActiveCategoryState(activeCard: LinearLayout, title: String, iconRes: Int) {
        // Matikan semua kartu terlebih dahulu
        for (card in cardsList) {
            card.setBackgroundResource(R.drawable.bg_card_category_inactive)
            val box = card.getChildAt(0) as FrameLayout
            val img = box.getChildAt(0) as ImageView
            val txt = card.getChildAt(1) as TextView

            box.setBackgroundResource(R.drawable.bg_icon_box_inactive)
            img.setColorFilter(Color.parseColor("#64748B")) // Warna abu-abu soft inactive
            txt.setTextColor(Color.parseColor("#64748B"))

            // Kembalikan label asli jika cardLainnya di-deselect
            if (card.id == R.id.cardLainnya && activeCard.id != R.id.cardLainnya) {
                txt.text = "Lainnya"
                img.setImageResource(R.drawable.ic_more_dots)
            }
        }

        // Aktifkan kartu terpilih
        activeCard.setBackgroundResource(R.drawable.bg_card_category_active)
        val activeBox = activeCard.getChildAt(0) as FrameLayout
        val activeImg = activeBox.getChildAt(0) as ImageView
        val activeTxt = activeCard.getChildAt(1) as TextView

        activeBox.setBackgroundResource(R.drawable.bg_icon_box_active)
        activeImg.setImageResource(iconRes)
        activeImg.setColorFilter(Color.parseColor("#2563EB")) // Tint warna biru aktif asli
        activeTxt.text = title
        activeTxt.setTextColor(Color.parseColor("#0F172A")) // Navy pekat teks aktif
        selectedKategori = title
    }

    private fun showSubCategoryBottomSheet(onItemClick: (CategoryItem) -> Unit) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_kategori, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvBottomSheetKategori)

        recyclerView.layoutManager = GridLayoutManager(this, 2) // Grid layout 2 kolom responsif
        recyclerView.adapter = BottomSheetCategoryAdapter(subKategoriList) { item ->
            onItemClick(item)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    // Data Class Model
    data class CategoryItem(val title: String, val iconRes: Int)

    // Adapter untuk internal Bottom Sheet Grid
    private class BottomSheetCategoryAdapter(
        private val items: List<CategoryItem>,
        private val clickListener: (CategoryItem) -> Unit
    ) : RecyclerView.Adapter<BottomSheetCategoryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.itemTvTitle)
            val ivIcon: ImageView = view.findViewById(R.id.itemIvIcon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category_bottom_sheet, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTitle.text = item.title
            holder.ivIcon.setImageResource(item.iconRes)
            holder.itemView.setOnClickListener { clickListener(item) }
        }

        override fun getItemCount(): Int = items.size
    }

    // Adapter kustom untuk Dropdown list pemilih gedung
    private inner class DropdownGedungAdapter(context: Context, items: Array<String>) :
        ArrayAdapter<String>(context, R.layout.item_dropdown_gedung, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent) as TextView
            view.text = getItem(position)

            // Berikan penanda warna highlight khusus pada pilihan yang sedang aktif saat ini
            if (position == selectedGedungPosition) {
                view.setBackgroundColor(Color.parseColor("#EFF6FF"))
                view.setTextColor(Color.parseColor("#2563EB"))
            } else {
                view.setBackgroundResource(R.drawable.bg_item_dropdown_selector)
                view.setTextColor(Color.parseColor("#0F172A"))
            }
            return view
        }
    }
}