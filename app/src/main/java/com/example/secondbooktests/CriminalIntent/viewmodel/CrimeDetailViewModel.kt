package com.example.secondbooktests.CriminalIntent.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.secondbooktests.CriminalIntent.CrimeRepository
import com.example.secondbooktests.CriminalIntent.classes.Crime
import java.io.File
import java.util.*


class CrimeDetailViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData:LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData) { crimeId ->
        crimeRepository.getCrime(crimeId)
    }
    fun loadCrime(crimeId:UUID){
        crimeIdLiveData.value = crimeId
    }
    fun saveCrime(crime:Crime){
        crimeRepository.updateCrime(crime)
    }

    fun getPhoto(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}