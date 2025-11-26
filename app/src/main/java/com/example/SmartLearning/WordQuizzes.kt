@file:Suppress("PackageName")

package com.example.SmartLearning


import android.content.Context

object WordQuizzes {
    const val TOTAL_QUESTION : String = "total_questions"
    const val CORRECT_ANSWERS: String = "correct_answers"
    fun WordQuestions3(context: Context): ArrayList<Question1> {
        val questionsList = ArrayList<Question1>()

        questionsList.add(
            Question1(
                1,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.ich),
                context.getString(R.string.ich_ar),
                context.getString(R.string.er_ar),
                context.getString(R.string.sie_ar),
                context.getString(R.string.wir_ar),
                1
            )
        )

        questionsList.add(
            Question1(
                2,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.du_ar),
                context.getString(R.string.ich),
                context.getString(R.string.du),
                context.getString(R.string.er),
                context.getString(R.string.wir),
                2
            )
        )

        questionsList.add(
            Question1(
                3,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.er),
                context.getString(R.string.ich_ar),
                context.getString(R.string.sie_ar),
                context.getString(R.string.er_ar),
                context.getString(R.string.wir_ar),
                3
            )
        )

        questionsList.add(
            Question1(
                4,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.sie_ar),
                context.getString(R.string.er),
                context.getString(R.string.du),
                context.getString(R.string.wir),
                context.getString(R.string.sie),
                4
            )
        )

        questionsList.add(
            Question1(
                5,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.es),
                context.getString(R.string.es_ar),
                context.getString(R.string.wir_ar),
                context.getString(R.string.er_ar),
                context.getString(R.string.ich_ar),
                1
            )
        )

        questionsList.add(
            Question1(
                6,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.wir),
                context.getString(R.string.dieser_ar),
                context.getString(R.string.wir_ar),
                context.getString(R.string.ihr_ar),
                context.getString(R.string.ich_ar),
                2
            )
        )

        questionsList.add(
            Question1(
                7,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.ihr),

                context.getString(R.string.personal_pronoun_ar),
                context.getString(R.string.sie_polite_ar),
                context.getString(R.string.ihr_ar),
                context.getString(R.string.du_ar),
                3
            )
        )

        questionsList.add(
            Question1(
                8,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.sie_polite_ar),
                context.getString(R.string.sie),
                context.getString(R.string.ihr),
                context.getString(R.string.er),
                context.getString(R.string.sie_polite),
                4
            )
        )

        questionsList.add(
            Question1(
                9,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.dieser_ar),
                context.getString(R.string.dieser),
                context.getString(R.string.er),
                context.getString(R.string.ich),
                context.getString(R.string.diese),
                1
            )
        )

        questionsList.add(
            Question1(
                10,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.dieses),
                context.getString(R.string.er_ar),
                context.getString(R.string.dieses_ar),
                context.getString(R.string.ihr_ar),
                context.getString(R.string.du_ar),
                2
            )
        )

        questionsList.add(
            Question1(
                11,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.dieses_ar),
                context.getString(R.string.dieser),
                context.getString(R.string.er),
                context.getString(R.string.dieses),
                context.getString(R.string.ihr),
                3
            )
        )

        questionsList.add(
            Question1(
                12,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.jener),
                context.getString(R.string.dieses_ar),
                context.getString(R.string.er_ar),
                context.getString(R.string.wir_ar),
                context.getString(R.string.jener_ar),
                4
            )
        )

        questionsList.add(
            Question1(
                13,
                context.getString(R.string.q2_qworld),
                context.getString(R.string.jenes_ar),
                context.getString(R.string.jenes),
                context.getString(R.string.ich),
                context.getString(R.string.er),
                context.getString(R.string.wir),
                1
            )
        )
        return questionsList.shuffled() as ArrayList<Question1>
    }



}







