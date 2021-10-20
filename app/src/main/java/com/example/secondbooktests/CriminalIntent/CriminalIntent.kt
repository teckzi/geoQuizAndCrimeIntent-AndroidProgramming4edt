package com.example.secondbooktests.CriminalIntent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.secondbooktests.R
import java.util.*

private const val TAG = "CriminalIntent"
class CriminalIntent : AppCompatActivity(),CrimeListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criminal_intent)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null){
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,fragment)
                .commit()
        }
    }

    override fun onCrimeSelected(crimeId: UUID) {
        Log.d(TAG,"MainActivity.onCrimeSelected: $crimeId" )
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }
}