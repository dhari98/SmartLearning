@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

/**
 * AppAbout
 * This Activity shows the "About App" screen where the user can:
 *  - View the current version of the application
 *  - Open communication/contact link (Linktree)
 *  - Share the app with others
 *  - Rate the app on Google Play
 */
class AppAbout : AppCompatActivity() {

    // UI sections (LinearLayouts acting like buttons)
    private lateinit var tocommunicate : LinearLayout
    private lateinit var shareLayout : LinearLayout
    private lateinit var rateLayout : LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_about)

        // 🔹 Initialize views
        tocommunicate = findViewById(R.id.tocommunicate)
        shareLayout = findViewById(R.id.shareLayout)
        rateLayout = findViewById(R.id.rateLayout)

        // 🔹 Retrieve the app version name dynamically and display it in a TextView
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionLabel = getString(R.string.textview)
        findViewById<TextView>(R.id.textView4).text = "$versionLabel $versionName"

        // 🔹 "Contact Developer" button — opens Linktree profile in browser
        tocommunicate.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = "https://linktr.ee/dharif98".toUri()
            startActivity(openURL)
        }

        // 🔹 "Share App" — opens Android Share Sheet to share Play Store link
        shareLayout.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_app_text, packageName)
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        // 🔹 "Rate App" — opens the Google Play page of the application
        rateLayout.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
            startActivity(openURL)
        }
    }
}
