package com.pmirkelam.tatatechnodemo.data.providers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.pmirkelam.tatatechnodemo.data.model.RandomTextResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

private const val TAG = "IavAppDataProvider"

class IavAppDataProvider(private val context: Context) {
    private val resolver: ContentResolver = context.contentResolver
    private val uri: Uri = Uri.parse("content://com.iav.contestdataprovider/text")
    private val gson = Gson()

    suspend fun fetchRandom(maxLength: Int): RandomTextResponse = retryWithBackoff(
        attempts = 3,
        baseDelayMs = 400
    ) {
        withTimeout(5_000) {
            Log.d(TAG, "Fetching random string with maxLength=$maxLength")

            val projection = arrayOf("data")
            val cursor = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val args = Bundle().apply { putInt(ContentResolver.QUERY_ARG_LIMIT, maxLength) }
                    Log.d(TAG, "Using QUERY_ARG_LIMIT for API >= 26")
                    resolver.query(uri, projection, args, null)
                } else {
                    Log.d(TAG, "Querying provider without args for API < 26")
                    resolver.query(uri, projection, null, null, null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying provider", e)
                throw e
            }

            cursor?.use { c ->
                if (!c.moveToFirst()) {
                    Log.e(TAG, "No rows returned from provider")
                    throw IllegalStateException("No rows from provider")
                }

                val idx = c.getColumnIndex("data")
                if (idx == -1) {
                    Log.e(TAG, "'data' column missing in cursor")
                    throw IllegalStateException("'data' column missing")
                }

                val jsonStr = try {
                    c.getString(idx)
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading JSON string from cursor", e)
                    throw e
                }

                Log.d(TAG, "Received JSON: $jsonStr")

                val response = try {
                    gson.fromJson(jsonStr, RandomTextResponse::class.java)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing JSON", e)
                    throw e
                }

                Log.d(TAG, "Parsed response: $response")

                return@withTimeout response
            }

            Log.e(TAG, "Provider returned null cursor")
            throw IllegalStateException("Provider returned null cursor")
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
            Log.d(TAG, "Attempt ${i + 1} of $attempts")
            return block()
        } catch (t: Throwable) {
            Log.e(TAG, "Attempt ${i + 1} failed", t)
            last = t
            if (i < attempts - 1) delay(baseDelayMs * (1L shl i))
        }
    }
    Log.e(TAG, "All $attempts attempts failed", last)
    throw last ?: IllegalStateException("Unknown error")
}
