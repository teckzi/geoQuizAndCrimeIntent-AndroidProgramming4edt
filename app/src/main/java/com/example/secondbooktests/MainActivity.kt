package com.example.secondbooktests

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.secondbooktests.CriminalIntent.CriminalIntent
import com.example.secondbooktests.GeoQuiz.GeoQuiz
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButtonOne.setOnClickListener{
            startActivity(Intent(this,GeoQuiz::class.java))
        }

        criminalIntentApp.setOnClickListener {
            startActivity(Intent(this,CriminalIntent::class.java))
        }
    }
}