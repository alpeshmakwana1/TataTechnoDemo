package com.pmirkelam.tatatechnodemo.data.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.pmirkelam.tatatechnodemo.data.local.RandomTextDao
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import com.pmirkelam.tatatechnodemo.data.providers.IavAppDataProvider
import kotlinx.coroutines.flow.Flow

class RandomTextRepository(
    private val provider: IavAppDataProvider,
    private val dao: RandomTextDao
) {
    val allTexts: Flow<List<RandomText>> = dao.getAll()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchAndSave(maxLength: Int) {
        val response = provider.fetchRandom(maxLength)
        dao.insert(response.randomText)
    }

    fun getAll(): Flow<List<RandomText>> = dao.getAll()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun generateRandom(length: Int): Result<Unit> {
        return try {
            val response = provider.fetchRandom(length)
            val entity = RandomText(
                value = response.randomText.value,
                length = response.randomText.length,
                created = response.randomText.created
            )
            dao.insert(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun delete(text: RandomText) {
        dao.delete(text)
    }
}