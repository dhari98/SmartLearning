@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.util.Locale

@SuppressLint("VisibleForTests", "CutPasteId")
class TextToSpeechActivity : AppCompatActivity() {
    private lateinit var mTTS: TextToSpeech
    private lateinit var mButtonSpeak: Button
    private lateinit var mEditText: EditText
    private lateinit var mSeekBarSpeed: SeekBar
    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_to_speech)

        mButtonSpeak = findViewById(R.id.button_speak)
        mEditText = findViewById(R.id.edit_text)
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed)

        // تهيئة الإعلانات
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        title = "تحويل النَص الى كلام"

        // تهيئة Text-to-Speech
        mTTS = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val res = mTTS.setLanguage(Locale.forLanguageTag("de-DE"))
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not Supported")
                } else {
                    mButtonSpeak.isEnabled = true
                }
            } else {
                Toast.makeText(applicationContext, "Error in converting", Toast.LENGTH_SHORT)
                    .show()
                Log.e("TTS", "Init Failed")
            }
        }

        // زر النطق
        mButtonSpeak.setOnClickListener {
            val text = mEditText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, getString(R.string.texttospeech), Toast.LENGTH_SHORT).show()
            } else {
                speak(text)
            }
        }
    }

    private fun speak(text: String) {
        val speed = (mSeekBarSpeed.progress.toFloat() / 50).coerceAtLeast(0.1f)

        mTTS.setPitch(1.0f) // قيمة ثابتة
        mTTS.setSpeechRate(speed)

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID")

        val result = mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
        if (result == TextToSpeech.ERROR) {
            Toast.makeText(applicationContext, "Error in converting", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        mTTS.stop()
        mTTS.shutdown()
        super.onDestroy()
    }
}
