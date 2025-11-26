@file:Suppress("PackageName")

package com.example.SmartLearning

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class AniImageMainActivity(private val context: Context, private val photos: List<Int>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.setImageResource(photos[position])

        // ✨ أهم سطرين
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.adjustViewBounds = true

        // نفس مقاسات الـ ViewPager
        val params = ViewGroup.LayoutParams(
            170.dp(context),   // width
            135.dp(context)    // height
        )
        imageView.layoutParams = params

        container.addView(imageView)
        return imageView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val imageView = `object` as ImageView
        viewPager.removeView(imageView)
    }

    override fun getCount(): Int {
        return photos.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    fun Int.dp(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

}