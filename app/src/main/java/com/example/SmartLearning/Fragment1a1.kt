@file:Suppress("PackageName")

package com.example.SmartLearning


import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import java.util.Locale


/**
 * Fragment1a1
 *
 * This fragment displays the German alphabet as a list of items using RecyclerView.
 * Each item can be pronounced using Text-To-Speech, marked as favorite, and includes
 * inline (native) ads that appear inside the list.
 *
 * Features used in this screen:
 *  ✔ RecyclerView + Animation
 *  ✔ Text-To-Speech (German)
 *  ✔ Native AdMob Ads inside the list
 */
class Fragment1a1 : Fragment() {

    private lateinit var recycler1a1: RecyclerView
    private lateinit var textToSpeech: TextToSpeech
    private var nativeAd: NativeAd? = null

    // Native AdMob test unit ID
    private val nativeAdUnitId = "ca-app-pub-3940256099942544/2247696110"

    // Adapter reference to update ad when loaded
    private lateinit var adapter: MainsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_fragment1a1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MobileAds.initialize(requireContext()) {}

        recycler1a1 = view.findViewById(R.id.recycler1a1)

        /**
         * Initialize Text-To-Speech
         * German voice → used to pronounce alphabet items
         */
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.GERMANY
            } else {
                Log.e("TTS", "Initialization failed.")
            }
        }

        /**
         * Apply layout animation to RecyclerView items
         * Gives a smooth appearance when the list loads
         */
        val lac = LayoutAnimationController(AnimationUtils.loadAnimation(requireContext(), R.anim.item_anim))
        lac.delay = 0.20f
        lac.order = LayoutAnimationController.ORDER_NORMAL
        recycler1a1.layoutAnimation = lac

        /**
         * Create the list of alphabet items.
         * Each Example4 contains:
         *  - like icon
         *  - sound icon
         *  - German letter
         *  - Example word
         *  - unique id
         */


        // تحضير قائمة البيانات
        val users1a1 = arrayListOf(
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.deutsches_alphabet), getString(R.string.german_alphabet) + " ", 0),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.a_example), getString(R.string.a_word) + " ", 1),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.b_example), getString(R.string.b_word) + " ", 2),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.c_example), getString(R.string.c_word) + " ", 3),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.d_example), getString(R.string.d_word) + " ", 4),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.e_example), getString(R.string.e_word) + " ", 5),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.f_example), getString(R.string.f_word) + " ", 6),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.g_example), getString(R.string.g_word) + " ", 7),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.h_example), getString(R.string.h_word) + " ", 8),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.i_example), getString(R.string.i_word) + " ", 9),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.j_example), getString(R.string.j_word) + " ", 10),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.k_example), getString(R.string.k_word) + " ", 11),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.l_example), getString(R.string.l_word) + " ", 12),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.m_example), getString(R.string.m_word) + " ", 13),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.n_example), getString(R.string.n_word) + " ", 14),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.o_example), getString(R.string.o_word) + " ", 15),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.p_example), getString(R.string.p_word) + " ", 16),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.q_example), getString(R.string.q_word) + " ", 17),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.r_example), getString(R.string.r_word) + " ", 18),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.s_example), getString(R.string.s_word) + " ", 19),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.t_example), getString(R.string.t_word) + " ", 20),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.u_example), getString(R.string.u_word) + " ", 21),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.v_example), getString(R.string.v_word) + " ", 22),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.w_example), getString(R.string.w_word) + " ", 23),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.x_example), getString(R.string.x_word) + " ", 24),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.y_example), getString(R.string.y_word) + " ", 25),
            Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.z_example), getString(R.string.z_word) + " ", 26)
        )

        /**
         * Step 1 — Initialize adapter without ads first.
         * When the ad is loaded later, the adapter gets updated.
         */
        adapter = MainsAdapter(
            exampleList = users1a1,
            textToSpeech = textToSpeech,
            showAd = false,
            adType = AdType.NATIVE,
            nativeAd = null
        )

        recycler1a1.layoutManager = LinearLayoutManager(requireContext())
        recycler1a1.adapter = adapter

        // Step 2 — Load Native Ad in the background
        loadNativeAd()

        /**
         * Step 3 — Ensure only ONE native ad is visible while scrolling
         * This improves UI quality and avoids showing multiple ads at once
         */
        recycler1a1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                var adVisible = false
                for (i in firstVisible..lastVisible) {
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
                    if (viewHolder is MainsAdapter.AdViewHolder) {
                        viewHolder.itemView.visibility = if (!adVisible) {
                            adVisible = true
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
            }
        })
    }

    /**
     * Load AdMob Native Ad and update the adapter once loaded
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun loadNativeAd() {
        val builder = AdLoader.Builder(requireContext(), nativeAdUnitId)

        builder.forNativeAd { ad: NativeAd ->
            nativeAd = ad

            // Update adapter to enable ad display
            adapter.showAd = true
            adapter.nativeAd = nativeAd

            // Refresh the list to insert ads
            adapter.notifyDataSetChanged()
        }

        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * Cleanup — Release TTS and Ad resource to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        nativeAd?.destroy()
    }
}