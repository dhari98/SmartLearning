@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class AppAbout : AppCompatActivity() {

    private lateinit var tocommunicate : LinearLayout
    private lateinit var shareLayout : LinearLayout
    private lateinit var rateLayout : LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_about)

        tocommunicate = findViewById(R.id.tocommunicate)
        shareLayout = findViewById(R.id.shareLayout)
        rateLayout = findViewById(R.id.rateLayout)



        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionLabel = getString(R.string.textview)
        findViewById<TextView>(R.id.textView4).text = "$versionLabel $versionName"



        tocommunicate.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = "https://linktr.ee/dharif98".toUri()
            startActivity(openURL)
        }

        shareLayout.setOnClickListener {
            val intent= Intent()
            intent.action= Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_app_text, packageName))
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))
        }
        rateLayout.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
            startActivity(openURL)
        }





    }
}