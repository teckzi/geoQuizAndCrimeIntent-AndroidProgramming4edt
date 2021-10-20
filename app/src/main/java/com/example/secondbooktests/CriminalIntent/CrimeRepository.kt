package com.example.secondbooktests.CriminalIntent

import  android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.secondbooktests.CriminalIntent.classes.Crime
import com.example.secondbooktests.CriminalIntent.database.CrimeDatabase
import com.example.secondbooktests.CriminalIntent.database.migration_2_3
import java.io.File
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(context: Context) {


    private val database:CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_2_3).build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()  // Исполнитель - объект, который ссылается на поток
        // newSingleThreadExecutor() возвращает экземпляр Исполнителя, котоырй указывает на новый поток,
        // работа выполняемая с исполнителем будет происходить вне основног опотока
    private val filesDir = context.applicationContext.filesDir
    fun getCrimes() : LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun updateCrime(crime:Crime){
        executor.execute{  //execute принимает на выполнение блок кода, который будет выполнен в любом потоке, на который ссылается Исполнитель
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime){
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    fun getPhotoFile(crime: Crime):File = File(filesDir,crime.photoFileName)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = CrimeRepository(context)
        }


        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}