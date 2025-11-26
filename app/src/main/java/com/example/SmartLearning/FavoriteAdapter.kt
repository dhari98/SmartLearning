@file:Suppress("PackageName")

package com.example.SmartLearning


import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * FavoriteAdapter
 *
 * RecyclerView adapter responsible for displaying German vocabulary cards
 * with support for:
 *  - Text-To-Speech pronunciation
 *  - Favorites toggle (Room database)
 *  - Native AdMob ads inserted dynamically inside the list
 *
 * The adapter supports 2 view types:
 *  1) Vocabulary card item (content)
 *  2) Native advertisement item
 *
 * Ad insertion policy:
 *  An ad is displayed every [adFrequency] items if ads are enabled and a valid
 *  NativeAd instance is available.
 */
@Suppress("ConstPropertyName")
class FavoriteAdapter(
    private var exampleList: ArrayList<Example4>,   // Main vocabulary list
    private val textToSpeech: TextToSpeech,         // Pronunciation engine
    private var showAd: Boolean = true,             // Controls Ad visibility
    private val adType: AdType = AdType.NATIVE,     // Requested ad format
    private var nativeAd: NativeAd? = null          // Loaded Native ad object
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0     // Vocabulary card view
        private const val VIEW_TYPE_AD = 1          // Native ad view
        private const val adFrequency = 4           // Insert ad every 4 items
    }

    /** Replaces current list (used after search or DB refresh) */
    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(newList: ArrayList<Example4>) {
        exampleList = newList
        notifyDataSetChanged()
    }

    /** Checks if ads should be injected inside the list */
    private fun adsEnabled(): Boolean = showAd && adType == AdType.NATIVE && nativeAd != null

    /** Determines which view type should be displayed at a given position */
    override fun getItemViewType(position: Int): Int {
        if (!adsEnabled()) return VIEW_TYPE_CONTENT
        return if ((position + 1) % (adFrequency + 1) == 0) VIEW_TYPE_AD else VIEW_TYPE_CONTENT
    }

    /** Count real items + ads positions */
    override fun getItemCount(): Int {
        return if (!adsEnabled()) exampleList.size
        else exampleList.size + (exampleList.size / adFrequency)
    }

    /** Creates either content view holder or ad view holder based on view type */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD && adsEnabled()) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad_native, parent, false)
            AdViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.mains_card_view1, parent, false)
            ExampleViewHolder(view, textToSpeech)
        }
    }

    /** Binds vocabulary items and ads to their respective view holders */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExampleViewHolder -> {
                // Calculate real index in the vocabulary list (ignoring ads)
                val actualPosition = if (adsEnabled()) position - (position / (adFrequency + 1)) else position
                if (actualPosition in 0 until exampleList.size) {
                    val item = exampleList[actualPosition]
                    holder.bind(item)
                    holder.handleFavorite(item)
                }
            }
            is AdViewHolder -> holder.bindNative(nativeAd)
        }
    }

    // --------------------- AD HOLDER ---------------------
    /**
     * ViewHolder responsible for rendering a Native AdMob ad
     * inside RecyclerView layout.
     */
    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNative(nativeAd: NativeAd?) {
            if (nativeAd == null) return

            val adView = itemView.findViewById<NativeAdView>(R.id.native_ad_view)
            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            val bodyView = adView.findViewById<TextView>(R.id.ad_body)

            // Bind ad assets
            adView.mediaView = mediaView
            adView.iconView = iconView
            adView.headlineView = headlineView
            adView.bodyView = bodyView

            headlineView.text = nativeAd.headline
            bodyView.text = nativeAd.body ?: ""

            nativeAd.icon?.let {
                iconView.setImageDrawable(it.drawable)
                iconView.visibility = View.VISIBLE
            } ?: run { iconView.visibility = View.GONE }

            nativeAd.mediaContent?.let {
                mediaView.mediaContent = it
                mediaView.visibility = View.VISIBLE
            } ?: run { mediaView.visibility = View.GONE }

            adView.setNativeAd(nativeAd)
        }
    }

    // ------------------ CONTENT HOLDER ------------------
    /**
     * ViewHolder responsible for:
     *  - Showing the vocabulary word + its meaning
     *  - Playing pronunciation using TTS
     *  - Adding or removing the word from favorites
     */
    class ExampleViewHolder(
        itemView: View,
        private val textToSpeech: TextToSpeech
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageViewLike: ImageView = itemView.findViewById(R.id.imageViewLike)
        private val imageViewSound: ImageView = itemView.findViewById(R.id.imageViewSound)
        private val textView1: TextView = itemView.findViewById(R.id.textViewWorld)
        private val textView2: TextView = itemView.findViewById(R.id.textViewWorld2)
        private var mydata: Example4? = null

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        /** Pronunciation button — uses TTS to speak the German word */
        init {
            imageViewSound.setOnClickListener {
                mydata?.let { data ->
                    if (textToSpeech.isSpeaking) textToSpeech.stop()
                    val txt = data.text1.replace("\n", " ").trim()
                    textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }

        /** Binds the vocabulary card UI */
        fun bind(data: Example4) {
            mydata = data
            textView1.text = data.text1
            textView2.text = data.text2
            imageViewSound.setImageResource(data.imageViewSound)
        }

        /** Handles favorites toggle and database update */
        fun handleFavorite(data: Example4) {
            val repo = FavoritesManager.repo(itemView.context)
            var isFav: Boolean

            scope.launch {
                // Check if word already exists in DB
                val exists = withContext(Dispatchers.IO) { repo.getById(data.id) }
                isFav = exists != null
                updateFavoriteIcon(isFav)

                // Toggle like/unlike
                imageViewLike.setOnClickListener {
                    isFav = !isFav
                    updateFavoriteIcon(isFav)

                    scope.launch(Dispatchers.IO) {
                        if (isFav) repo.add(data.copy(isFavorite = true))
                        else repo.remove(data)
                    }
                }
            }
        }

        /** Updates heart (favorite) icon based on state */
        private fun updateFavoriteIcon(isFav: Boolean) {
            imageViewLike.setImageResource(if (isFav) R.drawable.like else R.drawable.unlike)
        }
    }
}
