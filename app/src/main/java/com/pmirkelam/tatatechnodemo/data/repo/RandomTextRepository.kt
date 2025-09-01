package com.pmirkelam.tatatechnodemo.data.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.pmirkelam.tatatechnodemo.data.local.AppDatabase
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import com.pmirkelam.tatatechnodemo.data.providers.IavAppDataProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RandomTextRepository @Inject constructor(
    private val provider: IavAppDataProvider,
    private val appDatabase: AppDatabase
) {

    val randomTextDao = appDatabase.randomTextDao()

    fun getAll(): Flow<List<RandomText>> = randomTextDao.getAll()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateRandom(length: Int): Result<Unit> {
        return try {
            val response = provider.fetchRandom(length)
            val entity = RandomText(
                value = response.randomText.value,
                length = response.randomText.length,
                created = response.randomText.created
            )
            randomTextDao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAll() {
        randomTextDao.deleteAll()
    }

    suspend fun delete(text: RandomText) {
        randomTextDao.delete(text)
    }
}