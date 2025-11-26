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

        // Load views
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Detect if current theme is dark mode
        val isNightMode = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Text color for unselected tabs
        val normalTextColor = if (isNightMode) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.black)
        }

        // Text color for selected tab
        val selectedTextColor = if (isNightMode) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.black)
        }

        // Apply tab text colors
        tabLayout.setTabTextColors(normalTextColor, selectedTextColor)

        // Change underline indicator color under selected tab
        tabLayout.setSelectedTabIndicatorColor(
            ContextCompat.getColor(this, R.color.colorPrimary2)
        )

        // Receive type of group sent from MainActivity to decide which fragments to show
        val type = intent.getStringExtra("TYPE") ?: "GROUP1"

        // Get fragments & titles based on group type
        val (fragments, titles) = getFragmentsForType(type)

        // Set adapter for ViewPager
        viewPager.adapter = TabsPagerAdapter(this, fragments)

        // Connect TabLayout with ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    // Decide which fragments and titles will appear based on the type received from intent
    private fun getFragmentsForType(type: String): Pair<List<Fragment>, List<String>> {
        val context = this

        return when (type) {

            // GROUP1 → 3 repeated fragments with 3 tab titles
            "GROUP1" -> Pair(
                listOf(
                    Fragment1a1(),
                    Fragment1a1(),
                    Fragment1a1()
                ),
                listOf(
                    context.getString(R.string.Grammar),
                    context.getString(R.string.Readingandlistening),
                    context.getString(R.string.words)
                )
            )

            // GROUPNEVA1 → 2 fragments only (A1 section in locked mode)
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

            // GROUP3 → words, word test, audio test
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

            // If no valid type is received → return empty
            else -> Pair(emptyList(), emptyList())
        }
    }

    // ViewPager2 adapter to display the fragments
    class TabsPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {

        // Total number of fragments
        override fun getItemCount(): Int = fragments.size

        // Return fragment for each position
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
