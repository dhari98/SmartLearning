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

        val tvScore: TextView = findViewById(R.id.tv_score)
        val btnFinish: Button = findViewById(R.id.btn_finish)

        // احصل على البيانات من الاختبار
        val totalWordQuestions = intent.getIntExtra(WordQuizzes.TOTAL_QUESTION, 0)
        val correctWordAnswers = intent.getIntExtra(WordQuizzes.CORRECT_ANSWERS, 0)

        val totalAudioQuestions = intent.getIntExtra(AudioQuizzes.TOTAL_QUESTION, 0)
        val correctAudioAnswers = intent.getIntExtra(AudioQuizzes.CORRECT_ANSWERS, 0)

        val totalQuestions = totalWordQuestions + totalAudioQuestions
        val correctAnswers = correctWordAnswers + correctAudioAnswers

        tvScore.text = getString(R.string.score_text, correctAnswers, totalQuestions)

        // عند الضغط على زر "إنهاء"، يتم فتح آخر Fragment
        btnFinish.setOnClickListener {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val lastFragment = sharedPreferences.getString("LAST_FRAGMENT", null)

            if (lastFragment != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("FRAGMENT_NAME", lastFragment)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }
    }
}