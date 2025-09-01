package com.pmirkelam.tatatechnodemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pmirkelam.tatatechnodemo.data.model.RandomText

@Database(entities = [RandomText::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomTextDao(): RandomTextDao
}