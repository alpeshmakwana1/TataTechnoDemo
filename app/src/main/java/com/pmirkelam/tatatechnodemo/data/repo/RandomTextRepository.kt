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


//    suspend fun fetchAndSaveRandomText(length: Int) {
//        try {
//            val value = remote.getRandomText(length) // fetch from provider
//            val text = RandomText(
//                value = value,
//                length = value.length,
//                created = LocalDateTime.now().toString()
//            )
//            dao.insert(text)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // fallback or log error
//        }
//    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun delete(text: RandomText) {
        dao.delete(text)
    }
}