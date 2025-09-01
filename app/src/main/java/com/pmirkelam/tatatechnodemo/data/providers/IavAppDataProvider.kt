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

    suspend fun fetchRandom(maxLength: Int): RandomTextResponse = retryWithBackoff(
        attempts = 3,
        baseDelayMs = 400
    ) {
        withTimeout(5_000) {
            val projection = arrayOf("data")

            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Use QUERY_ARG_LIMIT for API 26+
                val args = Bundle().apply { putInt(ContentResolver.QUERY_ARG_LIMIT, maxLength) }
                resolver.query(uri, projection, args, null)
            } else {
                // For older versions, query without args
                resolver.query(uri, projection, null, null, null)
            }

            cursor?.use { c ->
                if (!c.moveToFirst()) error("No rows from provider")

                val idx = c.getColumnIndex("data")
                if (idx == -1) error("'data' column missing")

                val jsonStr = c.getString(idx)

                // Parse JSON
                val response = gson.fromJson(jsonStr, RandomTextResponse::class.java)

                // Truncate string manually if needed for older versions
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    val truncatedValue = response.randomText.value.take(maxLength)
//                    response.randomText = response.randomText.copy(value = truncatedValue)
//                }

                return@withTimeout response
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