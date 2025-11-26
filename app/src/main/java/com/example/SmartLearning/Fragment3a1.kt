@file:Suppress("PackageName")

package com.example.SmartLearning


import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import java.util.Locale


class Fragment3a1 : Fragment() {

    private lateinit var recycler3a1: RecyclerView
    private lateinit var textToSpeech: TextToSpeech
    private var nativeAd: NativeAd? = null

    private val nativeAdUnitId = "ca-app-pub-2017447968949849/5488886932"

    private lateinit var adapter: MainsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment3a1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MobileAds.initialize(requireContext()) {}

        recycler3a1 = view.findViewById(R.id.recycler3a1)

        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.GERMANY
            } else {
                Log.e("TTS", "Initialization failed.")
            }
        }

        val lac = LayoutAnimationController(AnimationUtils.loadAnimation(requireContext(), R.anim.item_anim))
        lac.delay = 0.20f
        lac.order = LayoutAnimationController.ORDER_NORMAL
        recycler3a1.layoutAnimation = lac


        val users3a1 = ArrayList<Example4>()



        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.personal_pronoun), getString(R.string.personal_pronoun_ar), 56))

        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.ich), getString(R.string.ich_ar), 57))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.du), getString(R.string.du_ar), 58))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.er), getString(R.string.er_ar), 59))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.sie), getString(R.string.sie_ar), 60))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.es), getString(R.string.es_ar), 61))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.wir), getString(R.string.wir_ar), 62))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.ihr), getString(R.string.ihr_ar), 63))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.sie_polite), getString(R.string.sie_polite_ar), 64))

        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.dieser), getString(R.string.dieser_ar), 65))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.diese), getString(R.string.diese_ar), 66))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.dieses), getString(R.string.dieses_ar), 67))

        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.jener), getString(R.string.jener_ar), 68))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.jene), getString(R.string.jene_ar), 69))
        users3a1.add(Example4(R.drawable.unlike, R.drawable.sound, getString(R.string.jenes), getString(R.string.jenes_ar), 70))


        adapter = MainsAdapter(
            exampleList = users3a1,
            textToSpeech = textToSpeech,
            showAd = false,
            adType = AdType.NATIVE,
            nativeAd = null
        )

        recycler3a1.layoutManager = LinearLayoutManager(requireContext())
        recycler3a1.adapter = adapter

        loadNativeAd()

        recycler3a1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun loadNativeAd() {
        val builder = AdLoader.Builder(requireContext(), nativeAdUnitId)

        builder.forNativeAd { ad: NativeAd ->
            nativeAd = ad

            adapter.showAd = true
            adapter.nativeAd = nativeAd

            adapter.notifyDataSetChanged()
        }

        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        nativeAd?.destroy()
    }
}