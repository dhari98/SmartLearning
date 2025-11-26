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
    private lateinit var viewPager: ViewPager        // Slider (image carousel at the top)
    private lateinit var aniImag: AniImageMainActivity  // Adapter for ViewPager image slider
    private lateinit var handler: Handler            // Handler to auto-slide ViewPager
    private lateinit var runnable: Runnable          // Runnable that changes images every delay

    private lateinit var recyclerView: RecyclerView  // Main categories list using GridLayout
    private lateinit var drawerlayout: DrawerLayout  // Navigation drawer main layout
    private lateinit var navview: NavigationView     // Navigation menu items

    private lateinit var toggle: ActionBarDrawerToggle // Drawer toggle for toolbar menu icon

    // 🔹 Ad-related variables
    private var mInterstitialAd: InterstitialAd? = null   // Interstitial ad (full screen ad)

    // 🔹 Other variables
    private val photos = listOf( // Slider images shown in ViewPager
        R.drawable.read,
        R.drawable.writ,
        R.drawable.watch,
        R.drawable.listen,
        R.drawable.speak,
        R.drawable.winner
    )

    private val delayTime: Long = 2000 // ⏳ Delay time between slider auto-switch (2 seconds)

    private val tag = "MainActivity" // Log tag for debugging
    private var counter = 0 // 🔢 Counter for clicks to show Interstitial Ad after every 4 clicks

    // 🔹 Categories list (RecyclerView)
    private lateinit var photoAdapter: MainActivityAdapter
    private var dataList = mutableListOf<DataMainActivity>()

    // 🔹 Tracks opened pages to avoid showing ad again for repeated access
    private val openedPages = mutableSetOf<String>()

    // 🔸 Loads Interstitial Ad for later display
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712", // Test ad unit
            adRequest,
            object : InterstitialAdLoadCallback() {
                // Ad loaded successfully
                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd = ad
                }
                // If ad fails to load
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main) // Set UI layout

        // ================== Initialize UI ==================
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        drawerlayout = findViewById(R.id.drawer_layout)
        navview = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerView)
        viewPager = findViewById(R.id.imageView1212)

        navview.itemIconTintList = null // Keep original colors for menu icons

        val menuItem = navview.menu.findItem(R.id.itema1)
        menuItem.title = getString(R.string.sub_item_1_1) // Default title before opening A1 page

        aniImag = AniImageMainActivity(this, photos) // Set slider adapter
        viewPager.adapter = aniImag

        // Auto-slide handler for ViewPager
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val nextPosition = (viewPager.currentItem + 1) % photos.size
                viewPager.setCurrentItem(nextPosition, true)
                handler.postDelayed(this, delayTime)
            }
        }

        title = "" // Removes app title from toolbar

        setSupportActionBar(toolbar) // Set toolbar as ActionBar

        // Initialize AdMob
        MobileAds.initialize(this) {}
        loadAds() // Load first Interstitial Ad

        // Setup drawer toggle (menu icon animation)
        toggle = ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        // ================== Navigation Drawer Click Handling ==================
        navview.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.itemfav -> { // Favorites page
                    val intent = Intent(this, FavoActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.germany -> { // Reserved for future feature (Germany Page)
                    true
                }

                // -------- A1 Module with Rewarded Ad Logic ----------
                R.id.itema1 -> {
                    val pageKey = "A1" // Unique key to track page open status

                    // If page opened before → open directly with no ad
                    if (openedPages.contains(pageKey)) {
                        val intent = Intent(this, TabsActivity::class.java)
                        intent.putExtra("TYPE", "GROUPNEVA1")
                        startActivity(intent)
                        return@setNavigationItemSelectedListener true
                    }

                    // Show confirmation dialog before showing ad
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.ad_alert_title))
                    builder.setMessage(getString(R.string.ad_alert_message))

                    builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        if (mInterstitialAd != null) {
                            // Setup ad callbacks
                            mInterstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {

                                    // When ad closes → open page
                                    override fun onAdDismissedFullScreenContent() {
                                        openedPages.add(pageKey)
                                        val intent = Intent(this@MainActivity, TabsActivity::class.java)
                                        intent.putExtra("TYPE", "GROUPNEVA1")
                                        startActivity(intent)
                                        loadInterstitialAd() // Reload next ad
                                        val navView = findViewById<NavigationView>(R.id.nav_view)
                                        val menuItem = navView.menu.findItem(R.id.itema1)
                                        menuItem.title = getString(R.string.sub_item_1_2) // Update title after first open
                                    }
                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        // If ad fails → open page normally
                                        Toast.makeText(this@MainActivity, getString(R.string.ad_failed_to_show), Toast.LENGTH_SHORT).show()
                                        openedPages.add(pageKey)
                                        val intent = Intent(this@MainActivity, TabsActivity::class.java)
                                        intent.putExtra("TYPE", "GROUPNEVA1")
                                        startActivity(intent)
                                        loadInterstitialAd()
                                        val navView = findViewById<NavigationView>(R.id.nav_view)
                                        val menuItem = navView.menu.findItem(R.id.itema1)
                                        menuItem.title = getString(R.string.sub_item_1_2)
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        mInterstitialAd = null // prevent showing same object twice
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

                // Share app
                R.id.itemsharapp -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text, packageName))
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(intent, "Share To:"))
                    true
                }

                // About page
                R.id.aboutapp -> {
                    startActivity(Intent(this, AppAbout::class.java))
                    true
                }

                // Rate the app
                R.id.starts -> {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
                    startActivity(openURL)
                    true
                }

                else -> super.onOptionsItemSelected(it)
            }
        }

        // Apply layout animation to RecyclerView items
        val lac = LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.item_anim))
        lac.delay = 0.20f
        lac.order = LayoutAnimationController.ORDER_NORMAL
        recyclerView.layoutAnimation = lac

        // Grid layout with dynamic span size depending on item type
        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (photoAdapter.getItemViewType(position)) {
                    MainActivityAdapter.TYPE_AD,
                    MainActivityAdapter.TYPE_TEXT -> 3 // Full-width row for title or ad
                    else -> 1
                }
            }
        }
        recyclerView.layoutManager = layoutManager

        // Main click listener for grid items
        photoAdapter = MainActivityAdapter(MainActivityAdapter.OnClickListener { data ->

            // Count only real lesson items (not ads / titles)
            if (data.image != 0 && data.image != null) {
                counter++
            }

            // Show interstitial ad every 4 clicks
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
                                launchACtivity(data)
                                mInterstitialAd = null
                                loadAds()
                            }
                        }
                    mInterstitialAd?.show(this)
                } else {
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

        ////////////////////////////////////////// Data (Levels & Lessons)
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

        photoAdapter.setDataList(dataList)
    }

    // Load interstitial ad for clicks outside the drawer navigation
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
                    mInterstitialAd = interstitialAd
                }
            })
    }

    // Launch the correct activity based on clicked item
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
            // Under development features
            13, 14, 15, 16 -> {
                Toast.makeText(this, getString(R.string.sub_item_2son), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Adds search functionality in toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView: SearchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false // No action required on submit
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText) // Filter list while user types
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Filters list based on user's search input
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
            Toast.makeText(baseContext, getString(R.string.item_not_available), Toast.LENGTH_SHORT).show()
        } else {
            photoAdapter.filterList(filteredList)
        }
    }

    // Updates favorite icon on resume to sync database changes
    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, delayTime) // Resume slider auto-play

        val menu = navview.menu
        val favoriteItem = menu.findItem(R.id.itemfav)

        val favoriteDao = FavoriteDatabase.getInstance(this).favoriteDao()

        CoroutineScope(Dispatchers.IO).launch {
            val favoriteList = favoriteDao.getAllFavorites()
            withContext(Dispatchers.Main) {
                if (favoriteList.isEmpty()) {
                    favoriteItem.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.unlike)
                } else {
                    favoriteItem.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.like)
                }
            }
        }
    }

    // Stop slider autoplay to avoid memory leaks
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}
