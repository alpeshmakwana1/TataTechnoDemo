package com.pmirkelam.tatatechnodemo.data.providers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.pmirkelam.tatatechnodemo.data.model.RandomTextResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

class IavAppDataProvider(private val context: Context) {
    private val resolver: ContentResolver = context.contentResolver
    private val uri: Uri = Uri.parse("content://com.iav.contestdataprovider/text")


    private val gson = Gson()


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchRandom(maxLength: Int): RandomTextResponse = retryWithBackoff(
        attempts = 3,
        baseDelayMs = 400
    ) {
        withTimeout(5_000) {
            val args = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, 1)   // only 1 row back
                putInt("length", maxLength)                  // provider-specific param
            }

            val projection = arrayOf("data")

            resolver.query(uri, projection, args, null)?.use { cursor ->
                if (!cursor.moveToFirst()) error("No rows from provider")

                val idx = cursor.getColumnIndex("data")
                if (idx == -1) error("'data' column missing")

                val jsonStr = cursor.getString(idx)
                return@withTimeout gson.fromJson(jsonStr, RandomTextResponse::class.java)
            }

            error("Provider returned null cursor")
        }
    }
}

private suspend fun <T> retryWithBackoff(
    attempts: Int,
    baseDelayMs: Long,
    block: suspend () -> T
): T {
    var last: Throwable? = null
    repeat(attempts) { i ->
        try {
            return block()
        } catch (t: Throwable) {
            last = t
            if (i < attempts - 1) delay(baseDelayMs * (1L shl i))
        }
    }
    throw last ?: IllegalStateException("Unknown error")
}