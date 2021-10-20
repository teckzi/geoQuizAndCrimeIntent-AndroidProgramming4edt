package com.example.secondbooktests.GeoQuiz

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.secondbooktests.R

private const val TAG = "GeoQuiz MainActivity"
private const val KEY_INDEX = "index"
private const val EXTRA_ANSWER_SHOWN = "qeoquiz_answer_shown"
//private const val REQUEST_CODE_CHEAT = 0


class GeoQuiz : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton:Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var countInt:TextView
    private lateinit var isCheater: TextView
    private lateinit var apiVersion:TextView
    private val quizViewModel:QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_geo_quiz)

        val provider = ViewModelProvider(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        savedBundle(savedInstanceState)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        countInt = findViewById(R.id.count)
        cheatButton = findViewById(R.id.cheat_button)
        isCheater = findViewById(R.id.isCheater)
        apiVersion = findViewById(R.id.APIversion)
        apiVersion.text = "API level ${Build.VERSION.SDK_INT}"
        questionTextView = findViewById(R.id.question_text_view)
        countInt.text = "Percent of correct answers:0%"
        questionTextView.setText(quizViewModel.currentQuestionText)

        //New StartActivityForResult
        val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                //quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false) ?: false
                quizViewModel.qListTwo[quizViewModel.currentIndex] = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false) ?: false
            }
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        prevButton.setOnClickListener{
            quizViewModel.moveToPrev()
            updateQuestion()
        }
        trueButton.setOnClickListener {
            checkAnswer(true)
        }
        falseButton.setOnClickListener {
            checkAnswer(false)
        }
        cheatButton.setOnClickListener {
            val intent = CheatActivity.newIntent(this,quizViewModel.currentQuestionAnswer)
            getResult.launch(intent)

            /*
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
            */

        }
        updateQuestion()


        }

    /* DeprecatedMethod

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
    */

    private fun updateQuestion(){
        questionTextView.setText(quizViewModel.currentQuestionText)

        if (quizViewModel.qList[quizViewModel.currentIndex]==1){
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }else {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
        val percent = String.format("%.0f",((100.0/6)*quizViewModel.count))
        countInt.text = "Percent of correct answers:$percent%"
        isCheater.text = "cheater control: ${quizViewModel.qListTwo[quizViewModel.currentIndex]}"
    }

    private fun checkAnswer(userAnswer:Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer
        quizViewModel.qList[quizViewModel.currentIndex] = 1

        val messageResId = when{
            quizViewModel.qListTwo[quizViewModel.currentIndex] -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        if (userAnswer == correctAnswer) {
            quizViewModel.count++
            quizViewModel.moveToNext()
            updateQuestion()
        }else{
            quizViewModel.moveToNext()
            updateQuestion()
        }



        Toast.makeText(this,messageResId, Toast.LENGTH_SHORT).show()

        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        updateQuestion()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt("COUNT_INDEX", quizViewModel.count)
        savedInstanceState.putIntArray("ARRAY_INDEX",quizViewModel.qList.toIntArray())
        savedInstanceState.putBooleanArray("isCheater_INDEX",quizViewModel.qListTwo.toBooleanArray())
    }
    fun savedBundle(savedInstanceState: Bundle?){
        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: 0
        quizViewModel.count = savedInstanceState?.getInt("COUNT_INDEX") ?: 0
        quizViewModel.qList = savedInstanceState?.getIntArray("ARRAY_INDEX")?.toMutableList() ?: mutableListOf(0,0,0,0,0,0)
        quizViewModel.qListTwo = savedInstanceState?.getBooleanArray("isCheater_INDEX")?.toMutableList() ?: mutableListOf(false,false,false,false,false,false)

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

}