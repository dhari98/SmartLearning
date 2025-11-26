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

    private lateinit var mTTS: TextToSpeech       // Text-to-Speech engine instance
    private lateinit var mButtonSpeak: Button     // Button used to trigger speech
    private lateinit var mEditText: EditText      // Input field for user text
    private lateinit var mSeekBarSpeed: SeekBar   // SeekBar to control speech speed
    private lateinit var mAdView: AdView          // Banner AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_to_speech)

        // Initialize views
        mButtonSpeak = findViewById(R.id.button_speak)
        mEditText = findViewById(R.id.edit_text)
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed)

        // Initialize AdMob banner
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Title of the Activity
        title = "تحويل النَص الى كلام"

        // Initialize Text-to-Speech engine
        mTTS = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set language to German (de-DE)
                val res = mTTS.setLanguage(Locale.forLanguageTag("de-DE"))
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not Supported")   // Language dependency missing / unsupported
                } else {
                    mButtonSpeak.isEnabled = true            // Enable button once TTS is ready
                }
            } else {
                Toast.makeText(applicationContext, "Error in converting", Toast.LENGTH_SHORT).show()
                Log.e("TTS", "Init Failed")                  // Failed to initialize TTS engine
            }
        }

        // Speak button click listener
        mButtonSpeak.setOnClickListener {
            val text = mEditText.text.toString().trim()     // Read text from input
            if (text.isEmpty()) {
                Toast.makeText(this, getString(R.string.texttospeech), Toast.LENGTH_SHORT).show()
            } else {
                speak(text)                                 // Call function to speak text
            }
        }
    }

    // Function that performs speech output
    private fun speak(text: String) {
        // Convert SeekBar value to speech rate (min value ≥ 0.1f)
        val speed = (mSeekBarSpeed.progress.toFloat() / 50).coerceAtLeast(0.1f)

        mTTS.setPitch(1.0f)               // Constant pitch value
        mTTS.setSpeechRate(speed)         // Set speech rate based on SeekBar

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID") // Utterance identifier

        // Execute speech
        val result = mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, params, "UniqueID")

        // Check for speech execution error
        if (result == TextToSpeech.ERROR) {
            Toast.makeText(applicationContext, "Error in converting", Toast.LENGTH_SHORT).show()
        }
    }

    // Release resources to prevent memory leaks
    override fun onDestroy() {
        mTTS.stop()         // Stop any ongoing speech
        mTTS.shutdown()     // Shutdown TTS engine
        super.onDestroy()
    }
}
