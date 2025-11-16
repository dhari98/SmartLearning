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
        val viewPager = container as ViewPager
        val imageView = ImageView(context)
        imageView.setImageResource(photos[position])
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        viewPager.addView(imageView)
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
}