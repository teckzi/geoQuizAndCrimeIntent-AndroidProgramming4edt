package com.example.secondbooktests.GeoQuiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.secondbooktests.R
import kotlin.jvm.internal.Intrinsics

private const val EXTRA_ANSWER_IS_TRUE = "qeoquiz.answer_is_true"
private const val EXTRA_ANSWER_SHOWN = "qeoquiz_answer_shown"
class CheatActivity : AppCompatActivity() {
    companion object{
        fun newIntent(packageContext: Context,answerIsTrue:Boolean):Intent{
            return Intent(packageContext,CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var answerIsTrue = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            val answerText = when{
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShowResult(true)
        }

    }

    private fun setAnswerShowResult(isAnswerShown:Boolean){
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown)
        }
        setResult(Activity.RESULT_OK,data)
    }


}