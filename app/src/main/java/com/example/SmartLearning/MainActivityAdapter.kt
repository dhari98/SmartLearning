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
        const val TYPE_IMAGE = 1
        const val TYPE_TEXT = 2
        const val TYPE_AD = 3
    }

    private var dataList = mutableListOf<DataMainActivity>()
    private var fullDataList = mutableListOf<DataMainActivity>()




    @SuppressLint("NotifyDataSetChanged")
    internal fun setDataList(dataList: List<DataMainActivity>) {
        val newList = mutableListOf<DataMainActivity>()

        // نسخ الـ IDs التي نريد أن نضيف تحتها إعلان
        val adUnderIds = setOf(18, 36,54)

        for (item in dataList) {
            newList.add(item)

            if (item.id in adUnderIds) {
                Log.d("AdInsert", "إضافة إعلان بعد العنصر ID = ${item.id}")
                newList.add(DataMainActivity(title = "", image = null, id = -1, customType = 3))
            }
        }



        this.dataList = newList
        this.fullDataList = newList
        notifyDataSetChanged()
    }




    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<DataMainActivity>) {
        dataList = filteredList.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_activity_cardview1, parent, false)
                ViewHolderWithImage(view)
            }
            TYPE_TEXT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_activity_cardview2, parent, false)
                ViewHolderWithText(view)
            }
            TYPE_AD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ad_banner_smal, parent, false)
                AdViewHolder(view)
            }
            else -> throw IllegalArgumentException("نوع غير معروف: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]

        if (holder is AdViewHolder) {
            val adRequest = AdRequest.Builder().build()
            holder.adView.loadAd(adRequest)
            return
        }

        if (holder is ViewHolderWithImage) {
            holder.title.text = data.title
            holder.image.setImageResource(data.image ?: R.drawable.about)
        }

        if (holder is ViewHolderWithText) {
            holder.title.text = data.title
        }

        holder.itemView.setOnClickListener {
            onClickListener.onClick(data)
        }
    }



    override fun getItemCount(): Int = dataList.size

    class ViewHolderWithImage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val title: TextView = itemView.findViewById(R.id.title)
    }

    class ViewHolderWithText(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
    }

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adView: AdView = itemView.findViewById(R.id.adView)
    }

    class OnClickListener(val clickListener: (data: DataMainActivity) -> Unit) {
        fun onClick(data: DataMainActivity) = clickListener(data)
    }
}

