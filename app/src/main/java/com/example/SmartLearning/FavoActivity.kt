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

class FavoActivity : AppCompatActivity() {

    private lateinit var mAdView: AdView
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favo)

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.GERMANY
            }
        }

        // Banner Ad
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rec)
        rv.setHasFixedSize(false)
        rv.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val favDao = FavoriteDatabase.getInstance(this@FavoActivity).favoriteDao()
            val fav = favDao.getAllFavorites()

            withContext(Dispatchers.Main) {
                val adapter = FavoriteAdapter(ArrayList(fav), textToSpeech) // ← تم الإصلاح
                rv.adapter = adapter

                if (adapter.itemCount == 0) {
                    Toast.makeText(this@FavoActivity, getString(R.string.empty_favorites), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
