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

class Fragment1a1 : Fragment() {

    private lateinit var recycler1a1: RecyclerView
    private lateinit var textToSpeech: TextToSpeech
    private var nativeAd: NativeAd? = null

    private val nativeAdUnitId = "ca-app-pub-2017447968949849/5488886932"

    // Adapter متغير ليتم تحديثه لاحقاً
    private lateinit var adapter: MainsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_fragment1a1, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MobileAds.initialize(requireContext()) {}

        recycler1a1 = view.findViewById(R.id.recycler1a1)

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
        recycler1a1.layoutAnimation = lac



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

        // 1. إنشاء Adapter بدون الإعلان أولاً (showAd=false, nativeAd=null)
        adapter = MainsAdapter(
            exampleList = users1a1,
            textToSpeech = textToSpeech,
            showAd = false,
            adType = AdType.NATIVE,
            nativeAd = null
        )

        recycler1a1.layoutManager = LinearLayoutManager(requireContext())
        recycler1a1.adapter = adapter

        // 2. تحميل الإعلان في الخلفية
        loadNativeAd()

        // 3. استمع للتمرير لإظهار إعلان واحد فقط حسب كودك
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

    @SuppressLint("NotifyDataSetChanged")
    private fun loadNativeAd() {
        val builder = AdLoader.Builder(requireContext(), nativeAdUnitId)

        builder.forNativeAd { ad: NativeAd ->
            // الإعلان تم تحميله
            nativeAd = ad

            // حدث المتغيرات في الـ Adapter وأبلغ الـ RecyclerView بالتغيير
            adapter.showAd = true
            adapter.nativeAd = nativeAd

            // نعيد تحديث الإعلان فقط (أو القائمة كلها)
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