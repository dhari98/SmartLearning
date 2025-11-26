@file:Suppress("PackageName")

package com.example.SmartLearning

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    // 🔹 UI components
    private lateinit var viewPager: ViewPager
    private lateinit var aniImag: AniImageMainActivity
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerlayout: DrawerLayout
    private lateinit var navview: NavigationView

    private lateinit var toggle: ActionBarDrawerToggle

    // 🔹 Ad-related variables
    private var mInterstitialAd: InterstitialAd? = null

    // 🔹 Other variables
    private val photos = listOf( // Slider images
        R.drawable.read,
        R.drawable.writ,
        R.drawable.watch,
        R.drawable.listen,
        R.drawable.speak,
        R.drawable.winner
    )

    private val delayTime: Long = 2000 // ⏳ Delay between photo switches (3 seconds)

    private val tag = "MainActivity" // 🔖 Log tag
    private var counter = 0 // 🔢 Counter for ads after every 4 clicks

    // 🔹 RecyclerView data
    private lateinit var photoAdapter: MainActivityAdapter
    private var dataList = mutableListOf<DataMainActivity>()

    // 🔹 Tracks opened pages (for example, to update menu titles)
    private val openedPages = mutableSetOf<String>()

    // 🔸 Load Interstitial Ad
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)





        // Initialize UI components
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        drawerlayout = findViewById(R.id.drawer_layout)
        navview = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerView)
        viewPager = findViewById(R.id.imageView1212)

        navview.itemIconTintList = null // Important: keep icons original color

        val menuItem = navview.menu.findItem(R.id.itema1)
        menuItem.title = getString(R.string.sub_item_1_1)

        aniImag = AniImageMainActivity(this, photos)
        viewPager.adapter = aniImag


        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val nextPosition = (viewPager.currentItem + 1) % photos.size
                viewPager.setCurrentItem(nextPosition, true)
                handler.postDelayed(this, delayTime)
            }
        }


        title = ""

        setSupportActionBar(toolbar)

        MobileAds.initialize(this) {}
        loadAds()

        toggle = ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()


        navview.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.itemfav -> {
                   val intent = Intent(this, FavoActivity::class.java)
                   startActivity(intent)
                    true
                }
                R.id.germany -> {
                //    val intent = Intent(this, GermaniActivity::class.java)
                //    startActivity(intent)
                    true
                }
                R.id.itema1 -> {
                    val pageKey = "A1"

                    if (openedPages.contains(pageKey)) {
                        val intent = Intent(this, TabsActivity::class.java)
                        intent.putExtra("TYPE", "GROUPNEVA1")
                        startActivity(intent)
                        return@setNavigationItemSelectedListener true
                    }

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.ad_alert_title))
                    builder.setMessage(getString(R.string.ad_alert_message))
                    builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        if (mInterstitialAd != null) {
                            mInterstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        openedPages.add(pageKey)

                                        // ✅ افتح TabsActivity بدل A1
                                        val intent =
                                            Intent(this@MainActivity, TabsActivity::class.java)
                                        intent.putExtra("TYPE", "GROUPNEVA1")
                                        startActivity(intent)
                                        loadInterstitialAd()

                                        // ✅ تحديث عنوان الصفحة في القائمة
                                        val navView: NavigationView = findViewById(R.id.nav_view)
                                        val menuItem = navView.menu.findItem(R.id.itema1)
                                        menuItem.title = getString(R.string.sub_item_1_2)
                                    }
                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        Toast.makeText(this@MainActivity, getString(R.string.ad_failed_to_show), Toast.LENGTH_SHORT).show()
                                        openedPages.add(pageKey)
                                        // ✅ افتح TabsActivity بدل A1
                                        val intent =
                                            Intent(this@MainActivity, TabsActivity::class.java)
                                        intent.putExtra("TYPE", "GROUPNEVA1")
                                        startActivity(intent)
                                        loadInterstitialAd()

                                        // ✅ تحديث عنوان الصفحة في القائمة
                                        val navView: NavigationView = findViewById(R.id.nav_view)
                                        val menuItem = navView.menu.findItem(R.id.itema1)
                                        menuItem.title = getString(R.string.sub_item_1_2)
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        mInterstitialAd = null
                                    }
                                }
                            mInterstitialAd?.show(this)
                        } else {
                            Toast.makeText(this, getString(R.string.ad_not_ready), Toast.LENGTH_SHORT).show()
                            loadInterstitialAd()
                        }
                        dialog.dismiss()
                    }

                    builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }

                    builder.show()
                    true
                }



                R.id.itemsharapp -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text, packageName))
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(intent, "Share To:"))
                    true
                }

                R.id.aboutapp -> {
                    val intent = Intent(this, AppAbout::class.java)
                    startActivity(intent)
                    true
                }

                R.id.starts -> {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data =
                        "https://play.google.com/store/apps/details?id=$packageName".toUri()
                    startActivity(openURL)
                    true
                }

                else -> super.onOptionsItemSelected(it)
            }
        }

        val lac = LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.item_anim))
        lac.delay = 0.20f
        lac.order = LayoutAnimationController.ORDER_NORMAL
        recyclerView.layoutAnimation = lac


        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (photoAdapter.getItemViewType(position)) {
                    MainActivityAdapter.TYPE_AD,
                    MainActivityAdapter.TYPE_TEXT -> 3 // يأخذ الصف كاملًا
                    else -> 1
                }
            }
        }
        recyclerView.layoutManager = layoutManager

        photoAdapter = MainActivityAdapter(MainActivityAdapter.OnClickListener { data ->

            // ✅ شرط لمنع عد العناصر العناوين أو الإعلانات
            if (data.image != 0 && data.image != null) {
                counter++
            }

            if (counter == 4) {
                counter = 0
                if (mInterstitialAd != null) {
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                launchACtivity(data)
                                mInterstitialAd = null
                                loadAds()
                            }
                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Log.d("AdError", adError.message)
                                launchACtivity(data)
                                mInterstitialAd = null
                                loadAds()
                            }
                        }
                    mInterstitialAd?.show(this)
                } else {
                    Log.d("launchActivity", "Interstitial ad wasn't ready.")
                    launchACtivity(data)
                    loadAds()
                }
            } else {
                launchACtivity(data)
            }
        })

        recyclerView.adapter = photoAdapter
        dataList.clear()
        recyclerView.adapter = photoAdapter
////////////////////////////////////////////////////////////////////////////////////////////////////
        dataList.add(DataMainActivity(getString(R.string.level_a1), 0, 0))

        dataList.add(DataMainActivity(getString(R.string.letters_pronunciation), R.drawable.alphabet, 1))
        dataList.add(DataMainActivity(getString(R.string.text_to_speech), R.drawable.texttospeech, 2))
        dataList.add(DataMainActivity(getString(R.string.personal_pronouns), R.drawable.person, 3))

        dataList.add(DataMainActivity(getString(R.string.introduction), R.drawable.handshake, 4))
        dataList.add(DataMainActivity(getString(R.string.greetings), R.drawable.introduction, 5))
        dataList.add(DataMainActivity(getString(R.string.numbers), R.drawable.numbers, 6))

        dataList.add(DataMainActivity(getString(R.string.time), R.drawable.time, 7))
        dataList.add(DataMainActivity(getString(R.string.days), R.drawable.days, 8))
        dataList.add(DataMainActivity(getString(R.string.months), R.drawable.annual, 9))

        dataList.add(DataMainActivity(getString(R.string.weather_seasons), R.drawable.season, 10))
        dataList.add(DataMainActivity(getString(R.string.family_friends), R.drawable.family, 11))
        dataList.add(DataMainActivity(getString(R.string.coolors), R.drawable.color, 12))




        dataList.add(DataMainActivity(getString(R.string.level_a2), 0, 13))
        dataList.add(DataMainActivity(getString(R.string.level_a3), 0, 14))
        dataList.add(DataMainActivity(getString(R.string.level_a4), 0, 15))
        dataList.add(DataMainActivity(getString(R.string.level_a5), 0, 16))


////////////////////////////////////////////////////////////////////////////////////////////////////

        photoAdapter.setDataList(dataList)

    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(tag, adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(tag, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun launchACtivity(data: DataMainActivity) {
        when (data.id) {
            1 -> {
                val intent = Intent(this, TabsActivity::class.java)
                intent.putExtra("TYPE", "GROUP1")
                startActivity(intent)
            }
            2 -> startActivity(Intent(this, TextToSpeechActivity::class.java))

            3 -> {
                val intent = Intent(this, TabsActivity::class.java)
                intent.putExtra("TYPE", "GROUP3")
                startActivity(intent)
            }

            ////////////////////////////////////////////////////////////////////////////////////////
            13 -> {
                Toast.makeText(this, getString(R.string.sub_item_2son), Toast.LENGTH_SHORT).show()
            }
            14 -> {
                Toast.makeText(this, getString(R.string.sub_item_2son), Toast.LENGTH_SHORT).show()
            }
            15 -> {
                Toast.makeText(this, getString(R.string.sub_item_2son), Toast.LENGTH_SHORT).show()
            }
            16 -> {
                Toast.makeText(this, getString(R.string.sub_item_2son), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Inflate the options menu and setup the SearchView listener
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView: SearchView =
            item?.actionView as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // No action needed on submit
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Filter the list as the user types
                filter(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    // Handle menu item clicks; here, only the search icon click is handled
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Filter the dataList based on the query text and update the adapter
    private fun filter(text: String) {
        val filteredList = ArrayList<DataMainActivity>()

        for (item in dataList) {
            if (item.title.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(baseContext, getString(R.string.item_not_available), Toast.LENGTH_SHORT)
                .show()
        } else {
            // Update the adapter with the filtered list
            photoAdapter.filterList(filteredList)
        }
    }

    // Update favorite icon in navigation drawer based on favorites presence in DB
    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, delayTime) // استئناف التبديل من الصورة الحالية


        val menu = navview.menu
        val favoriteItem = menu.findItem(R.id.itemfav)

        val favoriteDao = FavoriteDatabase.getInstance(this).favoriteDao()

        // Fetch favorites in background
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteList = favoriteDao.getAllFavorites()

            withContext(Dispatchers.Main) {
                // Change icon depending on whether favorites exist
                if (favoriteList.isEmpty()) {
                    favoriteItem.icon =
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.unlike)
                } else {
                    favoriteItem.icon =
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.like)
                }
            }
        }
    }


    // Remove pending callbacks to prevent memory leaks when activity pauses
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable) // إيقاف مؤقت عند الخروج مؤقتًا من الـActivity



    }
}