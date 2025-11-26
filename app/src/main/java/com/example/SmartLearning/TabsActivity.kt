@file:Suppress("PackageName")

package com.example.SmartLearning


import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TabsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)


        val isNightMode = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val normalTextColor = if (isNightMode) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.black)
        }

        val selectedTextColor = if (isNightMode) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.black)
        }

        tabLayout.setTabTextColors(normalTextColor, selectedTextColor)

        tabLayout.setSelectedTabIndicatorColor(
            ContextCompat.getColor(this, R.color.colorPrimary2)
        )



        // نحدد نوع المجموعة من خلال intent
        val type = intent.getStringExtra("TYPE") ?: "GROUP1"

        val (fragments, titles) = getFragmentsForType(type)

        // تعيين الـ Adapter للـ ViewPager
        viewPager.adapter = TabsPagerAdapter(this, fragments)

        // ربط الـ TabLayout بالـ ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }






    private fun getFragmentsForType(type: String): Pair<List<Fragment>, List<String>> {
        val context = this

        return when (type) {

            "GROUP1" -> Pair(
                listOf(
                    Fragment1a1(),
                    Fragment1a1(),
                    Fragment1a1()
                ),
                listOf(
                    context.getString(R.string.Grammar),
                    context.getString(R.string.Readingandlistening),
                    context.getString(R.string.words)   // ➕ تمت إضافة عنوان ثالث
                )
            )

            "GROUPNEVA1" -> Pair(
                listOf(
                    FragmentGrammarA1(),
                    FragmentGrammarA1()
                ),
                listOf(
                    context.getString(R.string.Grammar),
                    context.getString(R.string.Readingandlistening)
                )
            )

            "GROUP3" -> Pair(
                listOf(
                    Fragment3a1(),
                    Fragment3a2(),
                    Fragment3a3()
                ),
                listOf(
                    context.getString(R.string.words),
                    context.getString(R.string.word_test),
                    context.getString(R.string.audio_test)
                )
            )

            else -> Pair(emptyList(), emptyList())
        }
    }


    // الـ Adapter مدمج داخل نفس الكلاس
     class TabsPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}

