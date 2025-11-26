@file:Suppress("PackageName")

package com.example.SmartLearning

import android.content.Context
/**
 * AudioQuizzes
 * Responsible for generating audio-based quiz questions.
 * Each Question2 object contains:
 *  - id
 *  - question instruction
 *  - four answer options
 *  - correct option index
 */
object AudioQuizzes {

    // Key used to send the number of total questions to ResultActivity
    const val TOTAL_QUESTION: String = "TOTAL_QUESTIONS"

    // Key used to send the total correct answers to ResultActivity
    const val CORRECT_ANSWERS: String = "TOTAL_SCORE"


    /**
     * AudioQuestion3()
     * Generates a shuffled list of Question2 objects for the audio quiz level.
     * @param context — required to access string resources.
     * @return ArrayList<Question2> — randomized questions for the quiz.
     */
    @Suppress("FunctionName")
    fun AudioQuestion3(context: Context): ArrayList<Question2> {

        val questionsList = ArrayList<Question2>()

        // ✨ Each question takes:
        // (id, question text, option1, option2, option3, option4, correctAnswerIndex)
        questionsList.add(
            Question2(
                1, context.getString(R.string.audio_instruction),
                context.getString(R.string.personal_pronoun_ar), context.getString(R.string.ich_ar),
                context.getString(R.string.er_ar), context.getString(R.string.sie_ar), 1
            )
        )

        questionsList.add(
            Question2(
                2, context.getString(R.string.audio_instruction),
                context.getString(R.string.ich_ar), context.getString(R.string.sie_ar),
                context.getString(R.string.du_ar), context.getString(R.string.wir_ar), 1
            )
        )

        questionsList.add(
            Question2(
                3, context.getString(R.string.audio_instruction),
                context.getString(R.string.ich_ar), context.getString(R.string.du_ar),
                context.getString(R.string.sie_ar), context.getString(R.string.wir_ar), 2
            )
        )

        questionsList.add(
            Question2(
                4, context.getString(R.string.audio_instruction),
                context.getString(R.string.ich_ar), context.getString(R.string.sie_ar),
                context.getString(R.string.er_ar), context.getString(R.string.wir_ar), 3
            )
        )

        questionsList.add(
            Question2(
                5, context.getString(R.string.audio_instruction),
                context.getString(R.string.dieser_ar), context.getString(R.string.wir_ar),
                context.getString(R.string.diese_ar), context.getString(R.string.er_ar), 4
            )
        )

        questionsList.add(
            Question2(
                6, context.getString(R.string.audio_instruction),
                context.getString(R.string.er_ar), context.getString(R.string.sie_ar),
                context.getString(R.string.dieses_ar), context.getString(R.string.jene_ar), 2
            )
        )

        questionsList.add(
            Question2(
                7, context.getString(R.string.audio_instruction),
                context.getString(R.string.wir_ar), context.getString(R.string.sie_ar),
                context.getString(R.string.ihr_ar), context.getString(R.string.er_ar), 3
            )
        )

        questionsList.add(
            Question2(
                8, context.getString(R.string.audio_instruction),
                context.getString(R.string.sie_ar), context.getString(R.string.du_ar),
                context.getString(R.string.ich_ar), context.getString(R.string.wir_ar), 4
            )
        )

        questionsList.add(
            Question2(
                9, context.getString(R.string.audio_instruction),
                context.getString(R.string.dieser_ar), context.getString(R.string.wir_ar),
                context.getString(R.string.du_ar), context.getString(R.string.diese_ar), 1
            )
        )

        questionsList.add(
            Question2(
                10, context.getString(R.string.audio_instruction),
                context.getString(R.string.du_ar), context.getString(R.string.diese_ar),
                context.getString(R.string.wir_ar), context.getString(R.string.sie_ar), 2
            )
        )

        questionsList.add(
            Question2(
                11, context.getString(R.string.audio_instruction),
                context.getString(R.string.ich_ar), context.getString(R.string.diese_ar),
                context.getString(R.string.dieses_ar), context.getString(R.string.sie_ar), 3
            )
        )

        questionsList.add(
            Question2(
                12, context.getString(R.string.audio_instruction),
                context.getString(R.string.du_ar), context.getString(R.string.er_ar),
                context.getString(R.string.wir_ar), context.getString(R.string.jener_ar), 4
            )
        )

        // ✨ Randomize the question order before returning
        return questionsList.shuffled() as ArrayList<Question2>
    }
}
