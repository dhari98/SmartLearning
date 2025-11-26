@file:Suppress("PackageName")

package com.example.SmartLearning


import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("ConstPropertyName")
class MainsAdapter(
    private val exampleList: ArrayList<Example4>,
    private val textToSpeech: TextToSpeech,
    var showAd: Boolean = true,                  // Whether ads are enabled or not
    private val adType: AdType = AdType.BANNER,  // Type of ad: Banner or Native
    var nativeAd: NativeAd? = null               // Native ad instance when using NativeAd format
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0  // Normal content item
        private const val VIEW_TYPE_AD = 1       // Ad item inside RecyclerView
        private const val adFrequency = 9        // Insert an ad after every 9 content items
    }

    private var lastPosition = -1                // Used to animate items only once

    // Determines which layout type to use based on position
    override fun getItemViewType(position: Int): Int {
        if (!showAd) return VIEW_TYPE_CONTENT
        return if ((position + 1) % (adFrequency + 1) == 0) VIEW_TYPE_AD else VIEW_TYPE_CONTENT
    }

    // Calculates real item count including inserted ads
    override fun getItemCount(): Int {
        return if (!showAd) exampleList.size
        else exampleList.size + (exampleList.size / adFrequency)
    }

    // Inflates the correct ViewHolder layout depending on the view type (Content / Banner / Native)
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
            ExampleViewHolder(view, textToSpeech)
        }
    }

    // Binds data to the correct ViewHolder type
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {

            // Normal content item
            is ExampleViewHolder -> {
                val actualPosition = position - (position / (adFrequency + 1)) // Adjust index when ads appear
                if (actualPosition < exampleList.size) {
                    val data = exampleList[actualPosition]
                    holder.bind(data)
                    holder.handleFavorite(data)
                    setAnimation(holder.itemView, position)
                }
            }

            // Ad item
            is AdViewHolder -> {
                holder.bindAd(nativeAd, adType)
                setAnimation(holder.itemView, position)
            }
        }
    }

    // Item entrance animation (only when shown for the first time)
    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    // Remove animation when item is recycled
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    // --------------------- AD HOLDER ---------------------
    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Handles loading either Banner Ad or Native Ad
        fun bindAd(nativeAd: NativeAd?, adType: AdType) {
            // Banner Ad
            if (adType == AdType.BANNER) {
                val adView = itemView.findViewById<AdView>(R.id.adView)
                adView.loadAd(AdRequest.Builder().build())
                return
            }

            // Native Ad
            if (adType == AdType.NATIVE && nativeAd != null) {
                val adView = itemView.findViewById<NativeAdView>(R.id.native_ad_view)

                adView.headlineView = adView.findViewById(R.id.ad_headline)
                adView.bodyView = adView.findViewById(R.id.ad_body)
                adView.iconView = adView.findViewById(R.id.ad_app_icon)
                adView.mediaView = adView.findViewById(R.id.ad_media)

                // Fill ad components
                (adView.headlineView as TextView).text = nativeAd.headline
                (adView.bodyView as TextView).text = nativeAd.body ?: ""

                nativeAd.icon?.let {
                    (adView.iconView as ImageView).setImageDrawable(it.drawable)
                }

                adView.setNativeAd(nativeAd)
            }
        }
    }

    // --------------------- CONTENT HOLDER ---------------------
    class ExampleViewHolder(itemView: View, private val textToSpeech: TextToSpeech) :
        RecyclerView.ViewHolder(itemView) {

        private val imageLike: ImageView = itemView.findViewById(R.id.imageViewLike)
        private val imageSound: ImageView = itemView.findViewById(R.id.imageViewSound)
        private val text1: TextView = itemView.findViewById(R.id.textViewWorld)
        private val text2: TextView = itemView.findViewById(R.id.textViewWorld2)

        private var current: Example4? = null
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        // Binds text and sound icon
        fun bind(data: Example4) {
            current = data
            text1.text = data.text1
            text2.text = data.text2
            imageSound.setImageResource(data.imageViewSound)

            // Play Text-to-Speech sound
            imageSound.setOnClickListener {
                val txt = data.text1.replace("\n", " ").trim()
                textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        // Handles adding and removing favorites in database
        fun handleFavorite(data: Example4) {
            val repo = FavoritesManager.repo(itemView.context)

            scope.launch {
                var isFavorite = withContext(Dispatchers.IO) { repo.getById(data.id) != null }
                updateFavorite(isFavorite)

                imageLike.setOnClickListener {
                    isFavorite = !isFavorite
                    updateFavorite(isFavorite)

                    scope.launch(Dispatchers.IO) {
                        if (isFavorite) {
                            repo.add(data.copy(isFavorite = true))
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    itemView.context,
                                    itemView.context.getString(R.string.added_to_favorites),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            repo.remove(data)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    itemView.context,
                                    itemView.context.getString(R.string.removed_from_favorites),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        // Changes icon according to current favorite state
        private fun updateFavorite(isFav: Boolean) {
            imageLike.setImageResource(if (isFav) R.drawable.like else R.drawable.unlike)
        }
    }
}



/*

@Suppress("ConstPropertyName")
class MainsAdapter(
    private val exampleList: ArrayList<Example4>,
    private val textToSpeech: TextToSpeech,
    var showAd: Boolean = true,
    private val adType: AdType = AdType.BANNER,
    var nativeAd: NativeAd? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0
        private const val VIEW_TYPE_AD = 1
        private const val adFrequency = 9
    }

    private var lastPosition = -1

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
            ExampleViewHolder(view, textToSpeech)
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
                    setAnimation(holder.itemView, position)
                }
            }
            is AdViewHolder -> {
                holder.bindAd(nativeAd, adType)
                setAnimation(holder.itemView, position)
            }
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

 //   fun updateItem(position: Int) {
 //      notifyItemChanged(position)
 //   }

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
                val ctaButton = adView.findViewById<Button>(R.id.ad_call_to_action)
                val ratingBar = adView.findViewById<RatingBar>(R.id.ad_stars)

                adView.mediaView = mediaView
                adView.iconView = iconView
                adView.headlineView = headlineView
                adView.bodyView = bodyView
                adView.callToActionView = ctaButton
                adView.starRatingView = ratingBar

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

                nativeAd.starRating?.let {
                    ratingBar.rating = it.toFloat()
                    ratingBar.visibility = View.VISIBLE
                } ?: run { ratingBar.visibility = View.GONE }

                if (nativeAd.callToAction != null) {
                    val cta = nativeAd.callToAction!!.lowercase()
                    val isMisleading = (cta.contains("تحميل") || cta.contains("install") || cta.contains("download")) &&
                            nativeAd.store == null

                    ctaButton.text = if (isMisleading) "زيارة" else nativeAd.callToAction
                    ctaButton.visibility = View.VISIBLE
                } else {
                    ctaButton.visibility = View.GONE
                }

                adView.setNativeAd(nativeAd)
            }
        }
    }

    class ExampleViewHolder(itemView: View, private val textToSpeech: TextToSpeech) : RecyclerView.ViewHolder(itemView) {

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

                    val allowText2Ids = setOf(1,2,3,4,5,6,7,8,9,10,
                        11,12,13,14,15,16,17,18,19,20,
                        21,22,23,24,25,26,27,28,40,41,
                        42,43,44,45,46,47,48,49,50,51,
                        52,53,54,55)
                    val allowedIds = setOf(1,2,3,4,5,6,7,8,9,10,
                        11,12,13,14,15,16,17,18,19,20,
                        21,22,23,24,25,26,46,47,294)
                    val fullText1Ids = setOf(56,57,58,59,60,61,62,63,
                        64,65,66,67,68,69,70,71,72,73,
                        74,75,76,77,78,79,80,81,82,83,
                        84,85,86,87,88,89,90,91)

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
            val repository = FavoritesManager.repo(itemView.context)

            // 🔹 احفظ حالة المفضلة مبدئيًا لتحديث الأيقونة فورًا
            var isFavoriteNow: Boolean

            scope.launch {
                // قراءة الحالة الحالية
                val existing = withContext(Dispatchers.IO) { repository.getById(data.id) }
                isFavoriteNow = (existing != null)

                // تحديث الأيقونة مباشرة بدون انتظار
                updateFavoriteIcon(!isFavoriteNow)

                imageViewLike.setOnClickListener {
                    // عكس الحالة فورًا في الواجهة (بدون انتظار)
                    isFavoriteNow = !isFavoriteNow
                    updateFavoriteIcon(!isFavoriteNow)

                    // تشغيل العملية في الخلفية فقط
                    scope.launch(Dispatchers.IO) {
                        try {
                            if (isFavoriteNow) {
                                repository.add(data.copy(isFavorite = true))
                            } else {
                                repository.remove(data.copy(isFavorite = false))
                            }
                        } catch (_: Exception) {
                            // في حال فشل العملية (أوفلاين أو خطأ)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(itemView.context, itemView.context.getString(R.string.error_saving_favorite), Toast.LENGTH_SHORT).show()
                                // ارجع للحالة السابقة في حال فشل
                                isFavoriteNow = !isFavoriteNow
                                updateFavoriteIcon(!isFavoriteNow)
                            }
                        }
                    }

                    // ✅ نعرض رسالة خفيفة مباشرة
                    val msgRes = if (isFavoriteNow) R.string.added_to_favorites else R.string.removed_from_favorites
                    Toast.makeText(itemView.context, itemView.context.getString(msgRes), Toast.LENGTH_SHORT).show()
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
@Suppress("ConstPropertyName")
class MainsAdapter(
    private val exampleList: ArrayList<Example4>,
    private val textToSpeech: TextToSpeech,
    var showAd: Boolean = true,
    private val adType: AdType = AdType.BANNER,
    var nativeAd: NativeAd? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0
        private const val VIEW_TYPE_AD = 1
        private const val adFrequency = 9
    }

    private var lastPosition = -1

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

                    setAnimation(holder.itemView, position)
                }
            }
            is AdViewHolder -> {
                holder.bindAd(nativeAd, adType)

                setAnimation(holder.itemView, position)
            }
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        holder.itemView.clearAnimation()
        super.onViewDetachedFromWindow(holder)
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
                val ctaButton = adView.findViewById<Button>(R.id.ad_call_to_action)
                val ratingBar = adView.findViewById<RatingBar>(R.id.ad_stars)

                adView.mediaView = mediaView
                adView.iconView = iconView
                adView.headlineView = headlineView
                adView.bodyView = bodyView
                adView.callToActionView = ctaButton
                adView.starRatingView = ratingBar

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

                nativeAd.starRating?.let {
                    ratingBar.rating = it.toFloat()
                    ratingBar.visibility = View.VISIBLE
                } ?: run {
                    ratingBar.visibility = View.GONE
                }

                if (nativeAd.callToAction != null) {
                    val cta = nativeAd.callToAction!!.lowercase()
                    val isMisleading = (cta.contains("تحميل") || cta.contains("install") || cta.contains("download")) &&
                            nativeAd.store == null

                    ctaButton.text = if (isMisleading) "زيارة" else nativeAd.callToAction
                    ctaButton.visibility = View.VISIBLE
                } else {
                    ctaButton.visibility = View.GONE
                }

                adView.setNativeAd(nativeAd)
            }
        }
    }

    class ExampleViewHolder(
        itemView: View,
        private val textToSpeech: TextToSpeech,
        private val adapter: MainsAdapter
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

                    val text1 = data.text1.replace("\n", " ").trim()
                    val text1FirstLine = data.text1.split("\n").firstOrNull()?.trim() ?: ""
                    val text2FirstLine = data.text2.split("\n").firstOrNull()?.trim() ?: ""

                    val allowText2Ids = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                        21, 22, 23, 24, 25, 26, 27, 28, 40, 41,
                        42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
                        52, 53, 54, 55)

                    val allowedIds = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                        21, 22, 23, 24, 25, 26, 46, 47, 294)

                    val fullText1Ids = setOf(56, 57, 58, 59, 60, 61, 62, 63,
                        64, 65, 66, 67, 68, 69, 70, 71, 72, 73,
                        74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
                        84, 85, 86, 87, 88, 89, 90, 91)

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

