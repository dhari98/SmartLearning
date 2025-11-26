@file:Suppress("PackageName")

package com.example.SmartLearning

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.SmartLearning.databinding.Fragment3a2Binding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds


class Fragment3a2 : Fragment(), View.OnClickListener {

    // ====== Binding / Ads ======
    // ViewBinding reference for accessing layout views safely
    private var _binding: Fragment3a2Binding? = null
    private val binding get() = _binding!!
    private lateinit var mAdView: com.google.android.gms.ads.AdView

    // ====== Quiz State ======
    // Variables that store quiz progress and user selections
    private var currentPosition = 1
    private var questionsList: List<Question1> = emptyList()
    private var selectedOption = 0
    private var correctAnswers = 0
    private var isAnswerChecked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = Fragment3a2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdMob()       // Load and initialize AdMob banner ad
        setupQuiz()             // Prepare quiz questions and UI
        setClickListeners()     // Setup listeners for option buttons and submit button
    }

    /**
     * Initialize AdMob Banner Ad inside the fragment
     */
    private fun initializeAdMob() {
        MobileAds.initialize(requireContext()) {}
        mAdView = binding.adView
        mAdView.loadAd(AdRequest.Builder().build())
    }

    /**
     * Load questions and initialize quiz progress bar
     */
    private fun setupQuiz() {
        questionsList = WordQuizzes.WordQuestions3(requireContext())
        binding.progressBar.max = questionsList.size
        setQuestion()
    }

    /**
     * Attach click listeners to all answer options and submit button
     */
    private fun setClickListeners() = binding.apply {
        tvOptionOne.setOnClickListener(this@Fragment3a2)
        tvOptionTwo.setOnClickListener(this@Fragment3a2)
        tvOptionThree.setOnClickListener(this@Fragment3a2)
        tvOptionFour.setOnClickListener(this@Fragment3a2)
        btnSubmit.setOnClickListener(this@Fragment3a2)
    }

    /**
     * Display question, update UI and reset selection state
     */
    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        enableOptions(true)
        resetOptionStyles()
        isAnswerChecked = false
        selectedOption = 0

        val q = questionsList[currentPosition - 1]

        binding.apply {
            btnSubmit.text = getString(R.string.submit_answer)
            progressBar.progress = currentPosition
            tvProgress.text = "$currentPosition / ${progressBar.max}"

            tvQuestion.text = q.question
            ivImage.text = q.world
            tvOptionOne.text = q.optionOne
            tvOptionTwo.text = q.optionTwo
            tvOptionThree.text = q.optionThree
            tvOptionFour.text = q.optionFour
        }
    }

    /**
     * Reset answer buttons to default UI style each question
     */
    private fun resetOptionStyles() {
        listOf(binding.tvOptionOne, binding.tvOptionTwo, binding.tvOptionThree, binding.tvOptionFour).forEach { tv ->
            tv.setTextColor("#7A8089".toColorInt())
            tv.typeface = Typeface.DEFAULT
            tv.background = ContextCompat.getDrawable(requireContext(), R.drawable.option_design)
        }
    }

    /**
     * Handle clicks for answer options and submit button
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_option_one   -> selectOption(binding.tvOptionOne, 1)
            R.id.tv_option_two   -> selectOption(binding.tvOptionTwo, 2)
            R.id.tv_option_three -> selectOption(binding.tvOptionThree, 3)
            R.id.tv_option_four  -> selectOption(binding.tvOptionFour, 4)
            R.id.btn_submit      -> handleSubmit()
        }
    }

    /**
     * Update UI when user selects an answer option
     */
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

    /**
     * Handle submit button behavior depending on quiz state
     * First click = check answer
     * Second click = go to next question
     */
    private fun handleSubmit() {
        if (!isAnswerChecked) {
            if (selectedOption == 0) { toast(getString(R.string.select_answer_first)); return }
            checkAnswer()
        } else moveToNextQuestion()
    }

    /**
     * Validate selected answer, show feedback and update score
     */
    private fun checkAnswer() {
        val q = questionsList[currentPosition - 1]

        if (q.correctAnswer != selectedOption) {
            toast(getString(R.string.wrong_answer))
            highlightOption(selectedOption, R.drawable.wrong_option_border_bg)
        } else {
            toast(getString(R.string.correct_answer))
            correctAnswers++
        }

        highlightOption(q.correctAnswer, R.drawable.correct_option_border_bg)
        enableOptions(false)
        binding.btnSubmit.text = if (isLastQuestion()) getString(R.string.quiz_result) else getString(R.string.next_question)
        isAnswerChecked = true
    }

    /**
     * Move to next question or navigate to ResultActivity if finished
     */
    private fun moveToNextQuestion() {
        currentPosition++
        if (currentPosition <= questionsList.size) setQuestion() else showResults()
    }

    /**
     * Open ResultActivity and pass score + total questions
     */
    private fun showResults() {
        Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(WordQuizzes.TOTAL_QUESTION, questionsList.size)
            putExtra(WordQuizzes.CORRECT_ANSWERS, correctAnswers)
            startActivity(this)
        }
        requireActivity().finish()
    }

    /**
     * Highlight correct/incorrect selected answer
     */
    private fun highlightOption(option: Int, drawable: Int) {
        val view = when (option) {
            1 -> binding.tvOptionOne
            2 -> binding.tvOptionTwo
            3 -> binding.tvOptionThree
            4 -> binding.tvOptionFour
            else -> null
        }
        view?.background = ContextCompat.getDrawable(requireContext(), drawable)
    }

    /**
     * Enable/disable answer buttons after submitting
     */
    private fun enableOptions(state: Boolean) = binding.apply {
        tvOptionOne.isEnabled = state
        tvOptionTwo.isEnabled = state
        tvOptionThree.isEnabled = state
        tvOptionFour.isEnabled = state
    }

    /**
     * Check if the user reached the final question
     */
    private fun isLastQuestion() = currentPosition == questionsList.size

    private fun toast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    /**
     * Cleanup binding to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
