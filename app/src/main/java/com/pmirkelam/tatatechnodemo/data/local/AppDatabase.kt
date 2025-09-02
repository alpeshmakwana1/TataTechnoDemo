package com.pmirkelam.tatatechnodemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pmirkelam.tatatechnodemo.data.model.RandomText

/**
* App's Room database setup.
*
* Holds the database configuration and provides access to DAOs.
*/

@Database(entities = [RandomText::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomTextDao(): RandomTextDao
}