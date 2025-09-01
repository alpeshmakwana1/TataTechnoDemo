package com.pmirkelam.tatatechnodemo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomTextDao {
    @Query("SELECT * FROM random_texts ORDER BY id DESC")
    fun getAll(): Flow<List<RandomText>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RandomText)

    @Query("DELETE FROM random_texts")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(text: RandomText)
}