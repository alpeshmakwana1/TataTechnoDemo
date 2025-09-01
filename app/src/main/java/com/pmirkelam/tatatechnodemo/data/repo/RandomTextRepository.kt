package com.pmirkelam.tatatechnodemo.data.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pmirkelam.tatatechnodemo.data.local.AppDatabase
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import com.pmirkelam.tatatechnodemo.data.providers.IavAppDataProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val TAG = "RandomTextRepository"

class RandomTextRepository @Inject constructor(
    private val provider: IavAppDataProvider,
    private val appDatabase: AppDatabase
) {

    val randomTextDao = appDatabase.randomTextDao()

    fun getAll(): Flow<List<RandomText>> {
        Log.d(TAG, "Fetching all random texts from DB")
        return randomTextDao.getAll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateRandom(length: Int): Result<Unit> {
        return try {
            Log.d(TAG, "Generating random text of length $length")
            val response = provider.fetchRandom(length)
            val entity = RandomText(
                value = response.randomText.value,
                length = response.randomText.length,
                created = response.randomText.created
            )
            Log.d(TAG, "Inserting entity into DB: $entity")
            randomTextDao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate random text", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAll() {
        try {
            Log.d(TAG, "Deleting all random texts from DB")
            randomTextDao.deleteAll()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all random texts", e)
            throw e
        }
    }

    suspend fun delete(text: RandomText) {
        try {
            Log.d(TAG, "Deleting random text: $text")
            randomTextDao.delete(text)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete random text: $text", e)
            throw e
        }
    }
}
