@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.SmartLearning.Question2
import com.example.SmartLearning.databinding.Fragment3a3Binding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import java.util.Locale


class Fragment3a3 : Fragment(), View.OnClickListener {

    private lateinit var binding: Fragment3a3Binding
    private lateinit var tts: TextToSpeech
    private lateinit var mAdView: com.google.android.gms.ads.AdView

    private var currentPosition = 1
    private var questionsList: List<Question2> = emptyList()
    private var selectedOption = 0
    private var isAnswerChecked = false
    private var correctAnswers = 0

    private val germanNumbers by lazy {
        listOf(
            getString(R.string.personal_pronoun), getString(R.string.ich),
            getString(R.string.du), getString(R.string.er),
            getString(R.string.es), getString(R.string.sie),
            getString(R.string.ihr), getString(R.string.wir),
            getString(R.string.dieser), getString(R.string.diese),
            getString(R.string.dieses), getString(R.string.jener),
            getString(R.string.jene), getString(R.string.jenes)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = Fragment3a3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MobileAds.initialize(requireContext()) {}
        mAdView = binding.adView
        mAdView.loadAd(AdRequest.Builder().build())

        initializeTextToSpeech()

        setupQuiz()
        setClickListeners()
    }

    private fun initializeTextToSpeech() {
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.GERMANY
                tts.setSpeechRate(1.0f)
                tts.setPitch(1.0f)
            }
        }
    }

    private fun setupQuiz() {
        questionsList = AudioQuizzes.AudioQuestion3(requireContext())
        setQuestion()
    }

    private fun setClickListeners() = binding.apply {
        tvOptionOne.setOnClickListener(this@Fragment3a3)
        tvOptionTwo.setOnClickListener(this@Fragment3a3)
        tvOptionThree.setOnClickListener(this@Fragment3a3)
        tvOptionFour.setOnClickListener(this@Fragment3a3)
        btnSubmit.setOnClickListener(this@Fragment3a3)
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        enableOptions(true)
        resetOptionStyles()
        isAnswerChecked = false
        selectedOption = 0

        val q = questionsList[currentPosition - 1]

        binding.apply {
            btnSubmit.text = getString(R.string.submit_answer)
            tvQuestion.text = q.question
            if (currentPosition > 1) speakWord(q.id - 1)
            ivImage.setOnClickListener { speakWord(q.id - 1) }

            tvOptionOne.text = q.optionOne
            tvOptionTwo.text = q.optionTwo
            tvOptionThree.text = q.optionThree
            tvOptionFour.text = q.optionFour
        }
    }

    private fun speakWord(index: Int) {
        if (index in germanNumbers.indices) {
            tts.speak(germanNumbers[index], TextToSpeech.QUEUE_FLUSH, null, "utt_${System.nanoTime()}")
        }
    }

    private fun resetOptionStyles() {
        listOf(binding.tvOptionOne, binding.tvOptionTwo, binding.tvOptionThree, binding.tvOptionFour)
            .forEach { tv ->
                tv.setTextColor("#7A8089".toColorInt())
                tv.typeface = Typeface.DEFAULT
                tv.background = ContextCompat.getDrawable(requireContext(), R.drawable.option_design)
            }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_option_one -> selectOption(binding.tvOptionOne, 1)
            R.id.tv_option_two -> selectOption(binding.tvOptionTwo, 2)
            R.id.tv_option_three -> selectOption(binding.tvOptionThree, 3)
            R.id.tv_option_four -> selectOption(binding.tvOptionFour, 4)
            R.id.btn_submit -> handleSubmit()
        }
    }

    private fun selectOption(tv: TextView, optionNumber: Int) {
        if (isAnswerChecked) return
        resetOptionStyles()
        selectedOption = optionNumber

        tv.apply {
            setTextColor("#808080".toColorInt())
            setTypeface(typeface, Typeface.BOLD)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_option_border_bg)
        }
    }

    private fun handleSubmit() {
        if (isAnswerChecked) { moveToNextQuestion(); return }
        if (selectedOption == 0) { showToast(getString(R.string.select_answer_first)); return }
        checkAnswer()
    }

    private fun checkAnswer() {
        val q = questionsList[currentPosition - 1]

        if (q.correctAnswer != selectedOption) {
            showToast(getString(R.string.wrong_answer))
            highlightOption(selectedOption, R.drawable.wrong_option_border_bg)
        } else {
            showToast(getString(R.string.correct_answer))
            correctAnswers++
        }

        highlightOption(q.correctAnswer, R.drawable.correct_option_border_bg)
        enableOptions(false)
        binding.btnSubmit.text = if (isLastQuestion()) getString(R.string.quiz_result) else getString(R.string.next_question)
        isAnswerChecked = true
        selectedOption = 0
    }

    private fun moveToNextQuestion() {
        currentPosition++
        if (currentPosition <= questionsList.size) setQuestion() else showResults()
    }

    private fun showResults() {
        Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(WordQuizzes.TOTAL_QUESTION, questionsList.size)
            putExtra(WordQuizzes.CORRECT_ANSWERS, correctAnswers)
            startActivity(this)
        }
        requireActivity().finish()
    }

    private fun highlightOption(option: Int, backgroundRes: Int) {
        val tv = when (option) {
            1 -> binding.tvOptionOne
            2 -> binding.tvOptionTwo
            3 -> binding.tvOptionThree
            4 -> binding.tvOptionFour
            else -> null
        }
        tv?.background = ContextCompat.getDrawable(requireContext(), backgroundRes)
    }

    private fun enableOptions(enable: Boolean) = binding.apply {
        tvOptionOne.isEnabled = enable
        tvOptionTwo.isEnabled = enable
        tvOptionThree.isEnabled = enable
        tvOptionFour.isEnabled = enable
    }

    private fun isLastQuestion() = currentPosition == questionsList.size

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onPause() {
        super.onPause()
        if (::tts.isInitialized) tts.stop()
    }

    override fun onDestroy() {
        if (::tts.isInitialized) tts.shutdown()
        super.onDestroy()
    }
}
