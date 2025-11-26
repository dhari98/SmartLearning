@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class ResultActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // UI widgets from layout
        val tvScore: TextView = findViewById(R.id.tv_score)
        val btnFinish: Button = findViewById(R.id.btn_finish)

        // 🔹 Receive quiz data sent from Word / Audio quiz screens
        val totalWordQuestions = intent.getIntExtra(WordQuizzes.TOTAL_QUESTION, 0)
        val correctWordAnswers = intent.getIntExtra(WordQuizzes.CORRECT_ANSWERS, 0)

        val totalAudioQuestions = intent.getIntExtra(AudioQuizzes.TOTAL_QUESTION, 0)
        val correctAudioAnswers = intent.getIntExtra(AudioQuizzes.CORRECT_ANSWERS, 0)

        // 🔹 Combine both quiz types (Word + Audio)
        val totalQuestions = totalWordQuestions + totalAudioQuestions
        val correctAnswers = correctWordAnswers + correctAudioAnswers

        // 🔹 Display score using formatted string from resources
        tvScore.text = getString(R.string.score_text, correctAnswers, totalQuestions)

        // 🔹 Handle finish button click
        btnFinish.setOnClickListener {

            // Get the last opened fragment saved earlier in shared preferences
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val lastFragment = sharedPreferences.getString("LAST_FRAGMENT", null)

            // If a last fragment exists → open MainActivity and show that fragment
            if (lastFragment != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("FRAGMENT_NAME", lastFragment)
                startActivity(intent)
                finish()
            } else {
                // Otherwise just close ResultActivity
                finish()
            }
        }
    }
}
