@file:Suppress("PackageName")

package com.example.SmartLearning

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * AniImageMainActivity
 * PagerAdapter used to display a list of images inside a ViewPager with fixed dimensions.
 *
 * @param context the Context used to create ImageView components
 * @param photos a list of drawable resource IDs to be displayed in the ViewPager
 */
class AniImageMainActivity(private val context: Context, private val photos: List<Int>) : PagerAdapter() {

    /**
     * Creates and returns a new ImageView for the given ViewPager position.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)

        // Set the image resource based on the current position
        imageView.setImageResource(photos[position])

        // Prevent image distortion — keeps original ratio while fitting inside the ViewPager
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.adjustViewBounds = true

        // Apply the same fixed width/height as the ViewPager (in dp conversion)
        val params = ViewGroup.LayoutParams(
            170.dp(context),   // width
            135.dp(context)    // height
        )
        imageView.layoutParams = params

        // Add the ImageView into the ViewPager container
        container.addView(imageView)
        return imageView
    }

    /**
     * Removes the ImageView when it is no longer needed.
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val imageView = `object` as ImageView
        viewPager.removeView(imageView)
    }

    /**
     * Returns total number of images to display.
     */
    override fun getCount(): Int {
        return photos.size
    }

    /**
     * Verifies whether the provided view belongs to the object returned by instantiateItem().
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    /**
     * Extension function — converts dp to px based on device density.
     */
    fun Int.dp(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()
}
