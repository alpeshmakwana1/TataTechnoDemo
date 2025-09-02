package com.pmirkelam.tatatechnodemo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing RandomText data.
 *
 * Provides methods to insert, delete, and fetch random text entries.
 */
@Dao
interface RandomTextDao {
    /**
     * Returns all RandomText entries in descending order by ID.
     */
    @Query("SELECT * FROM random_texts ORDER BY id DESC")
    fun getAll(): Flow<List<RandomText>>

    /**
     * Inserts a RandomText item. Replaces on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RandomText)

    /**
     * Deletes all entries from the table.
     */
    @Query("DELETE FROM random_texts")
    suspend fun deleteAll()

    /**
     * Deletes a specific RandomText item.
     */
    @Delete
    suspend fun delete(text: RandomText)
}
