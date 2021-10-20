package com.example.secondbooktests.CriminalIntent.viewmodel

import androidx.lifecycle.ViewModel
import com.example.secondbooktests.CriminalIntent.CrimeRepository
import com.example.secondbooktests.CriminalIntent.classes.Crime

class CrimeListViewModel:ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime (crime:Crime){
        crimeRepository.addCrime(crime)
    }
}

