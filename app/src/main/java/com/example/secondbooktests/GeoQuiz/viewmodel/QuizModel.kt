package com.example.secondbooktests.GeoQuiz

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.secondbooktests.GeoQuiz.classes.Question
import com.example.secondbooktests.R

class QuizViewModel: ViewModel() {

    var currentIndex = 0
    var count = 0
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    var qList= mutableListOf(0,0,0,0,0,0)
    var qListTwo = mutableListOf(false,false,false,false,false,false)

    val currentQuestionAnswer:Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText:Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext(){
        if (currentIndex != 5)
        currentIndex = (currentIndex + 1) % questionBank.size
        Log.d("TAG","$currentIndex")
    }
    fun moveToPrev(){
        if (currentIndex >= 1)
        currentIndex = (currentIndex - 1) % questionBank.size
        Log.d("TAG","$currentIndex")
    }
}