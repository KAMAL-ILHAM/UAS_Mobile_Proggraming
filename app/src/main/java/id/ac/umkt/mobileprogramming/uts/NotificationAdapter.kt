package id.ac.umkt.mobileprogramming.uts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

// Sealed class tetap sama
sealed class NotificationItem {
    data class Header(val title: String) : NotificationItem()
    data class Data(
        val title: String,
        val desc: String,
        val time: String,
        var isUnread: Boolean,
        val iconRes: Int,
        val iconBgColor: Int,
        val documentId: String = "",
        val tiket: String = "",
        val kategori: String = "",
        val lokasi: String = "",
        val status: String = ""
    ) : NotificationItem()
}

class NotificationAdapter(
    private var items: List<NotificationItem>,
    private val onNotificationClick: (NotificationItem.Data) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    // Fungsi untuk memperbarui list saat filter atau status dibaca berubah
    fun updateData(newItems: List<NotificationItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationItem.Header -> TYPE_HEADER
            is NotificationItem.Data -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_notification_header, parent, false))
        } else {
            DataViewHolder(inflater.inflate(R.layout.item_notification, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotificationItem.Header -> (holder as HeaderViewHolder).bind(item)
            is NotificationItem.Data -> (holder as DataViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSectionHeader: TextView = view.findViewById(R.id.tvSectionHeader)
        fun bind(item: NotificationItem.Header) {
            tvSectionHeader.text = item.title
        }
    }

    inner class DataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)
        private val dotUnread: View = view.findViewById(R.id.dotUnread)
        private val iconContainer: FrameLayout = view.findViewById(R.id.iconContainer)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)

        fun bind(item: NotificationItem.Data) {
            tvTitle.text = item.title
            tvDesc.text = item.desc
            tvTime.text = item.time
            ivIcon.setImageResource(item.iconRes)
            iconContainer.backgroundTintList = ContextCompat.getColorStateList(itemView.context, item.iconBgColor)
            dotUnread.visibility = if (item.isUnread) View.VISIBLE else View.INVISIBLE

            tvTitle.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (item.isUnread) R.color.textNavy else R.color.textSecondary
                )
            )

            itemView.setOnClickListener {
                val context = itemView.context

                // Pisahkan logika pengumuman sistem dan detail laporan
                val intent = if (item.title.contains("Pemeliharaan") || item.title.contains("Sistem")) {
                    Intent(context, PengumumanDetailActivity::class.java)
                } else {
                    Intent(context, NotificationDetailActivity::class.java).apply {
                        // Bawa data ini ke halaman NotificationDetailActivity
                        putExtra("EXTRA_TITLE", item.title)
                        putExtra("EXTRA_DESC", item.desc)
                        putExtra("EXTRA_TIME", item.time)
                        putExtra("EXTRA_TIKET", item.tiket)
                        putExtra("EXTRA_KATEGORI", item.kategori)
                        putExtra("EXTRA_LOKASI", item.lokasi)
                        putExtra("EXTRA_DOC_ID", item.documentId)
                    }
                }
                context.startActivity(intent)

                // Trigger logika baca (menghilangkan titik biru)
                onNotificationClick(item)
            }
        }
    }
}