@file:Suppress("PackageName")

package com.example.SmartLearning

data class Question2(

    val id:Int,
    val question: String,

    val optionOne : String,
    val optionTwo : String,
    val optionThree : String,
    val optionFour : String,

    val correctAnswer: Int)