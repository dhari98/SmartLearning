@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
class MainActivityAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_IMAGE = 1      // View type → card with image
        const val TYPE_TEXT = 2       // View type → title row (full width)
        const val TYPE_AD = 3         // View type → banner ad inside RecyclerView
    }

    private var dataList = mutableListOf<DataMainActivity>()   // Current data shown in RecyclerView
    private var fullDataList = mutableListOf<DataMainActivity>() // Full original data (useful for search)

    @SuppressLint("NotifyDataSetChanged")
    internal fun setDataList(dataList: List<DataMainActivity>) {
        val newList = mutableListOf<DataMainActivity>()

        // IDs where an ad should be inserted right after the item
        val adUnderIds = setOf(3, 9)

        for (item in dataList) {
            newList.add(item) // Add the item normally

            // Insert an AdView item after specific IDs
            if (item.id in adUnderIds) {
                Log.d("AdInsert", "إضافة إعلان بعد العنصر ID = ${item.id}")
                newList.add(
                    DataMainActivity(
                        title = "",
                        image = null,
                        id = -1,
                        customType = 3   // customType = TYPE_AD → create ad row
                    )
                )
            }
        }

        this.dataList = newList
        this.fullDataList = newList // Save copy for filtering
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<DataMainActivity>) {
        // Update the list with search result
        dataList = filteredList.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        // Each item already contains 'type' (image / text / ad)
        return dataList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                // Lesson item with image + title
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_activity_cardview1, parent, false)
                ViewHolderWithImage(view)
            }
            TYPE_TEXT -> {
                // Section title (A1 – A2 etc.)
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_activity_cardview2, parent, false)
                ViewHolderWithText(view)
            }
            TYPE_AD -> {
                // Banner ad as a RecyclerView row
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ad_banner_smal, parent, false)
                AdViewHolder(view)
            }
            else -> throw IllegalArgumentException("نوع غير معروف: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]

        // Load AdMob banner when it's an ad item
        if (holder is AdViewHolder) {
            val adRequest = AdRequest.Builder().build()
            holder.adView.loadAd(adRequest)
            return
        }

        // Normal item with image
        if (holder is ViewHolderWithImage) {
            holder.title.text = data.title
            holder.image.setImageResource(data.image ?: R.drawable.about)
        }

        // Normal item with text only
        if (holder is ViewHolderWithText) {
            holder.title.text = data.title
        }

        // Click listener for all non-ad rows
        holder.itemView.setOnClickListener {
            onClickListener.onClick(data)
        }
    }

    override fun getItemCount(): Int = dataList.size // Total items (including ads when inserted)

    // ------- ViewHolders -------
    class ViewHolderWithImage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image) // Image on the card
        val title: TextView = itemView.findViewById(R.id.title) // Lesson title
    }

    class ViewHolderWithText(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title) // Section title row
    }

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adView: AdView = itemView.findViewById(R.id.adView) // Small banner ad
    }

    // Custom click listener to return clicked DataMainActivity object
    class OnClickListener(val clickListener: (data: DataMainActivity) -> Unit) {
        fun onClick(data: DataMainActivity) = clickListener(data)
    }
}
