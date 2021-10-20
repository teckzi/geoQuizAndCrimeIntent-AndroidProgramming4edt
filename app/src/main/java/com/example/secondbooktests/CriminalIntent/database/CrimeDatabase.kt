package com.example.secondbooktests.CriminalIntent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.secondbooktests.CriminalIntent.classes.Crime

@Database(entities = [Crime::class], version=3)
@TypeConverters(CrimeTypeConverter::class)
abstract class CrimeDatabase: RoomDatabase() {
    abstract fun crimeDao():CrimeDao
}
val migration_2_3 = object : Migration(2,3){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
    }

}