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

@Suppress("ConstPropertyName")
class FavoriteAdapter(
    private var exampleList: ArrayList<Example4>,
    private val textToSpeech: TextToSpeech,
    private var showAd: Boolean = true,
    private val adType: AdType = AdType.NATIVE,
    private var nativeAd: NativeAd? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0
        private const val VIEW_TYPE_AD = 1
        private const val adFrequency = 4
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(newList: ArrayList<Example4>) {
        exampleList = newList
        notifyDataSetChanged()
    }

    private fun adsEnabled(): Boolean = showAd && adType == AdType.NATIVE && nativeAd != null

    override fun getItemViewType(position: Int): Int {
        if (!adsEnabled()) return VIEW_TYPE_CONTENT
        return if ((position + 1) % (adFrequency + 1) == 0) VIEW_TYPE_AD else VIEW_TYPE_CONTENT
    }

    override fun getItemCount(): Int {
        return if (!adsEnabled()) exampleList.size
        else exampleList.size + (exampleList.size / adFrequency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD && adsEnabled()) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad_native, parent, false)
            AdViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.mains_card_view1, parent, false)
            ExampleViewHolder(view, textToSpeech)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExampleViewHolder -> {
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
    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNative(nativeAd: NativeAd?) {
            if (nativeAd == null) return

            val adView = itemView.findViewById<NativeAdView>(R.id.native_ad_view)
            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            val bodyView = adView.findViewById<TextView>(R.id.ad_body)

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

        init {
            imageViewSound.setOnClickListener {
                mydata?.let { data ->
                    if (textToSpeech.isSpeaking) textToSpeech.stop()
                    val txt = data.text1.replace("\n", " ").trim()
                    textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }

        fun bind(data: Example4) {
            mydata = data
            textView1.text = data.text1
            textView2.text = data.text2
            imageViewSound.setImageResource(data.imageViewSound)
        }

        fun handleFavorite(data: Example4) {
            val repo = FavoritesManager.repo(itemView.context)
            var isFav: Boolean

            scope.launch {
                val exists = withContext(Dispatchers.IO) { repo.getById(data.id) }
                isFav = exists != null
                updateFavoriteIcon(isFav)

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

        private fun updateFavoriteIcon(isFav: Boolean) {
            imageViewLike.setImageResource(if (isFav) R.drawable.like else R.drawable.unlike)
        }
    }
}





/*
//لاصلي مع داتا الفايربيس مع اعلان بانر مدمج
@Suppress("ConstPropertyName")
class FavoriteAdapter(
    private var exampleList: ArrayList<Example4>,
    private val textToSpeech: android.speech.tts.TextToSpeech,
    private var showAd: Boolean = true,
    private val adType: AdType = AdType.BANNER,
    private var nativeAd: NativeAd? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0
        private const val VIEW_TYPE_AD = 1
        private const val adFrequency = 4
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(newList: ArrayList<Example4>) {
        this.exampleList = newList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (!showAd) return VIEW_TYPE_CONTENT
        return if ((position + 1) % (adFrequency + 1) == 0) VIEW_TYPE_AD else VIEW_TYPE_CONTENT
    }

    override fun getItemCount(): Int {
        return if (!showAd) exampleList.size
        else exampleList.size + (exampleList.size / adFrequency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD) {
            val layout = if (adType == AdType.NATIVE)
                R.layout.item_ad_native
            else
                R.layout.item_ad_banner

            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            AdViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mains_card_view1, parent, false)
            ExampleViewHolder(view, textToSpeech, this)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExampleViewHolder -> {
                val actualPosition = position - (position / (adFrequency + 1))
                if (actualPosition < exampleList.size) {
                    val item = exampleList[actualPosition]
                    holder.bind(item)
                    holder.handleFavorite(item)
                }
            }
            is AdViewHolder -> holder.bindAd(nativeAd, adType)
        }
    }

    fun updateItem(position: Int) {
        notifyItemChanged(position)
    }

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindAd(nativeAd: NativeAd?, adType: AdType) {
            if (adType == AdType.BANNER) {
                val adView = itemView.findViewById<AdView>(R.id.adView)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            } else if (adType == AdType.NATIVE && nativeAd != null) {
                val adView = itemView.findViewById<NativeAdView>(R.id.native_ad_view)
                val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                val bodyView = adView.findViewById<TextView>(R.id.ad_body)

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
    }

    class ExampleViewHolder(
        itemView: View,
        private val textToSpeech: android.speech.tts.TextToSpeech,
        private val adapter: FavoriteAdapter
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageViewLike: ImageView = itemView.findViewById(R.id.imageViewLike)
        private val imageViewSound: ImageView = itemView.findViewById(R.id.imageViewSound)
        private val textView1: TextView = itemView.findViewById(R.id.textViewWorld)
        private val textView2: TextView = itemView.findViewById(R.id.textViewWorld2)
        private var mydata: Example4? = null

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        init {
            imageViewSound.setOnClickListener {
                mydata?.let { data ->
                    if (textToSpeech.isSpeaking) textToSpeech.stop()
                    val text1 = data.text1.replace("\n", " ").trim()
                    val text1FirstLine = data.text1.split("\n").firstOrNull()?.trim() ?: ""
                    val text2FirstLine = data.text2.split("\n").firstOrNull()?.trim() ?: ""

                    // نفس منطقك السابق
                    val allowText2Ids = setOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55)
                    val allowedIds = setOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,46,47,294)
                    val fullText1Ids = setOf(56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91)

                    when (data.id) {
                        in fullText1Ids -> textToSpeech.speak(text1, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                        in allowedIds -> {
                            val firstWord = text1FirstLine.split(" ").firstOrNull() ?: ""
                            textToSpeech.speak(firstWord, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                        else -> textToSpeech.speak(text1FirstLine, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                    if (data.id in allowText2Ids) {
                        textToSpeech.speak(text2FirstLine, android.speech.tts.TextToSpeech.QUEUE_ADD, null, null)
                    }
                }
            }
        }

        fun bind(data: Example4) {
            textView1.text = data.text1
            textView2.text = data.text2
            imageViewSound.setImageResource(data.imageViewSound)
            mydata = data
        }

        fun handleFavorite(data: Example4) {
            val repository = FavoritesManager.repo(itemView.context)

            scope.launch {
                val item = withContext(Dispatchers.IO) { repository.getById(data.id) }
                withContext(Dispatchers.Main) {
                    updateFavoriteIcon(item == null)

                    imageViewLike.setOnClickListener {
                        scope.launch {
                            val isNotFav = (repository.getById(data.id) == null)
                            if (isNotFav) {
                                repository.add(data.copy(isFavorite = true))
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(itemView.context, itemView.context.getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show()
                                    updateFavoriteIcon(false)
                                }
                            } else {
                                repository.remove(data.copy(isFavorite = false))
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(itemView.context, itemView.context.getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show()
                                    updateFavoriteIcon(true)
                                }
                            }
                            adapter.updateItem(bindingAdapterPosition)
                        }
                    }
                }
            }
        }

        private fun updateFavoriteIcon(isUnliked: Boolean) {
            imageViewLike.setImageResource(if (isUnliked) R.drawable.unlike else R.drawable.like)
        }
    }
}
 */








/*
//الاصلي بدون داتا الفايربيس
@Suppress("ConstPropertyName")
class FavoriteAdapter(private val exampleList: List<Example4>,
    private val textToSpeech: TextToSpeech,
    private var showAd: Boolean = true,
    private val adType: AdType = AdType.BANNER,
    private var nativeAd: NativeAd? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0
        private const val VIEW_TYPE_AD = 1
        private const val adFrequency = 4
    }

    override fun getItemViewType(position: Int): Int {
        if (!showAd) return VIEW_TYPE_CONTENT
        return if ((position + 1) % (adFrequency + 1) == 0) VIEW_TYPE_AD else VIEW_TYPE_CONTENT
    }

    override fun getItemCount(): Int {
        return if (!showAd) exampleList.size
        else exampleList.size + (exampleList.size / adFrequency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD) {
            val layout = if (adType == AdType.NATIVE)
                R.layout.item_ad_native
            else
                R.layout.item_ad_banner

            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            AdViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mains_card_view1, parent, false)
            ExampleViewHolder(view, textToSpeech, this)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExampleViewHolder -> {
                val actualPosition = position - (position / (adFrequency + 1))
                if (actualPosition < exampleList.size) {
                    val item = exampleList[actualPosition]
                    holder.bind(item)
                    holder.handleFavorite(item)
                }
            }
            is AdViewHolder -> {
                holder.bindAd(nativeAd, adType)
            }
        }
    }

    fun updateItem(position: Int) {
        notifyItemChanged(position)
    }

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindAd(nativeAd: NativeAd?, adType: AdType) {
            if (adType == AdType.BANNER) {
                val adView = itemView.findViewById<AdView>(R.id.adView)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            } else if (adType == AdType.NATIVE && nativeAd != null) {
                val adView = itemView.findViewById<NativeAdView>(R.id.native_ad_view)

                val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                val bodyView = adView.findViewById<TextView>(R.id.ad_body)

                adView.mediaView = mediaView
                adView.iconView = iconView
                adView.headlineView = headlineView
                adView.bodyView = bodyView

                headlineView.text = nativeAd.headline
                bodyView.text = nativeAd.body ?: ""

                nativeAd.icon?.let {
                    iconView.setImageDrawable(it.drawable)
                    iconView.visibility = View.VISIBLE
                } ?: run {
                    iconView.visibility = View.GONE
                }

                nativeAd.mediaContent?.let {
                    mediaView.mediaContent = it
                    mediaView.visibility = View.VISIBLE
                } ?: run {
                    mediaView.visibility = View.GONE
                }

                adView.setNativeAd(nativeAd)
            }
        }
    }

    class ExampleViewHolder(
        itemView: View,
        private val textToSpeech: TextToSpeech,
        private val adapter: FavoriteAdapter
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageViewLike: ImageView = itemView.findViewById(R.id.imageViewLike)
        private val imageViewSound: ImageView = itemView.findViewById(R.id.imageViewSound)
        private val textView1: TextView = itemView.findViewById(R.id.textViewWorld)
        private val textView2: TextView = itemView.findViewById(R.id.textViewWorld2)
        private var mydata: Example4? = null

        init {
            imageViewSound.setOnClickListener {
                mydata?.let { data ->
                    if (textToSpeech.isSpeaking) textToSpeech.stop()

                    val allowText2Ids = setOf(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                        21, 22, 23, 24, 25, 26, 27, 28, 40, 41,
                        42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
                        52, 53, 54, 55
                    )

                    val allowedIds = setOf(
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
                        17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 46, 47, 294
                    )

                    val fullText1Ids = setOf(
                        56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
                        69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
                        81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91
                    )

                    val text1 = data.text1.replace("\n", " ").trim()
                    val text1FirstLine = data.text1.split("\n").firstOrNull()?.trim() ?: ""
                    val text2FirstLine = data.text2.split("\n").firstOrNull()?.trim() ?: ""

                    when (data.id) {
                        in fullText1Ids -> textToSpeech.speak(text1, TextToSpeech.QUEUE_FLUSH, null, null)
                        in allowedIds -> {
                            val firstWord = text1FirstLine.split(" ").firstOrNull() ?: ""
                            textToSpeech.speak(firstWord, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                        else -> textToSpeech.speak(text1FirstLine, TextToSpeech.QUEUE_FLUSH, null, null)
                    }

                    if (data.id in allowText2Ids) {
                        textToSpeech.speak(text2FirstLine, TextToSpeech.QUEUE_ADD, null, null)
                    }
                }
            }
        }

        fun bind(data: Example4) {
            textView1.text = data.text1
            textView2.text = data.text2
            imageViewSound.setImageResource(data.imageViewSound)
            mydata = data
        }

        fun handleFavorite(data: Example4) {
            val databaseDao = FavoriteDatabase.getInstance(itemView.context).favoriteDao()

            CoroutineScope(Dispatchers.IO).launch {
                val item = databaseDao.getItem(data.id)

                withContext(Dispatchers.Main) {
                    updateFavoriteIcon(item == null)

                    imageViewLike.setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            toggleFavorite(data, databaseDao, item == null)

                            withContext(Dispatchers.Main) {
                                adapter.updateItem(bindingAdapterPosition)
                            }
                        }
                    }
                }
            }
        }

        private suspend fun toggleFavorite(data: Example4, dao: FavoriteDao, isFavorite: Boolean) {
            if (isFavorite) {
                dao.addData(data.copy(isFavorite = true))
                withContext(Dispatchers.Main) {
                    Toast.makeText(itemView.context, itemView.context.getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show()
                    updateFavoriteIcon(false)
                }
            } else {
                dao.delete(data.copy(isFavorite = false))
                withContext(Dispatchers.Main) {
                    Toast.makeText(itemView.context, itemView.context.getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show()
                    updateFavoriteIcon(true)
                }
            }
        }

        private fun updateFavoriteIcon(isUnliked: Boolean) {
            imageViewLike.setImageResource(if (isUnliked) R.drawable.unlike else R.drawable.like)
        }
    }
}

*/
