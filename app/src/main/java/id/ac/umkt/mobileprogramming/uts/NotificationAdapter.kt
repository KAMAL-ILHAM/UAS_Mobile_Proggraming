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
        val isUnread: Boolean,
        val iconRes: Int,
        val iconBgColor: Int
    ) : NotificationItem()
}

class NotificationAdapter(private val items: List<NotificationItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
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

            // --- PERBAIKAN LOGIKA KLIK ---
            itemView.setOnClickListener {
                val context = itemView.context

                // Cek judul notifikasi untuk menentukan halaman tujuan
                val intent = if (item.title == "Pemeliharaan Server") {
                    // Jika yang diklik adalah "Pemeliharaan Server", arahkan ke Pengumuman Detail
                    Intent(context, PengumumanDetailActivity::class.java)
                } else {
                    // Jika yang diklik lainnya (contoh: Teknisi Menuju Lokasi), arahkan ke Detail biasa
                    Intent(context, NotificationDetailActivity::class.java)
                }

                context.startActivity(intent)
            }
        }
    }
}