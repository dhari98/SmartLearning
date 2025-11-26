@file:Suppress("PackageName")

package com.example.SmartLearning

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
/**
 * FavoActivity
 *
 * Activity responsible for displaying and managing the list of user's
 * favorite German vocabulary. Data is loaded from Room database and
 * rendered in a RecyclerView. Each vocabulary item also includes
 * pronunciation support using Text-To-Speech (TTS).
 *
 * Main Features:
 *  - Loads saved favorites from local Room database asynchronously
 *  - Displays vocabulary in a RecyclerView using FavoriteAdapter
 *  - Supports pronunciation for each word using Google TTS
 *  - Shows Google AdMob Banner Ad at the bottom of the screen
 *
 * UX Notes:
 *  - If the Favorites list is empty, a friendly Toast message is shown
 *  - TTS resources are safely released in onDestroy() to avoid memory leaks
 */
class FavoActivity : AppCompatActivity() {

    private lateinit var mAdView: AdView                 // Banner AdView reference
    private lateinit var textToSpeech: TextToSpeech       // Text-To-Speech engine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favo)

        // Initialize Text-To-Speech for word pronunciation
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.GERMANY
            }
        }

        // Initialize and load Google AdMob Banner Ad
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Setup RecyclerView for displaying favorite items
        val rv = findViewById<RecyclerView>(R.id.rec)
        rv.setHasFixedSize(false)
        rv.layoutManager = LinearLayoutManager(this)

        // Load favorites from Room DB using coroutine (background thread)
        CoroutineScope(Dispatchers.IO).launch {
            val favDao = FavoriteDatabase.getInstance(this@FavoActivity).favoriteDao()
            val fav = favDao.getAllFavorites()

            // Switch back to main thread to update UI
            withContext(Dispatchers.Main) {
                val adapter = FavoriteAdapter(ArrayList(fav), textToSpeech)
                rv.adapter = adapter

                // If list is empty, show a Toast message
                if (adapter.itemCount == 0) {
                    Toast.makeText(
                        this@FavoActivity,
                        getString(R.string.empty_favorites),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        // Release TTS resources to prevent memory leaks
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
