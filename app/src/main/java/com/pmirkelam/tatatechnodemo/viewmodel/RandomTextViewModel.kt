package com.pmirkelam.tatatechnodemo.viewmodel

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import com.pmirkelam.tatatechnodemo.data.repo.RandomTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RandomTextViewModel"

@HiltViewModel
class RandomTextViewModel @Inject constructor(
    private val repo: RandomTextRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val allTexts: StateFlow<List<RandomText>> =
        repo.getAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun generateRandom(length: Int) {
        Log.d(TAG, "Request to generate random text of length $length")
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repo.generateRandom(length)
                if (result.isFailure) {
                    val msg = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e(TAG, "Failed to generate random text: $msg")
                    _error.emit("Failed: $msg")
                } else {
                    Log.d(TAG, "Random text generated successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in generateRandom", e)
                _error.emit("Failed: ${e.message}")
            }finally {
                _loading.value = false
            }
        }
    }

    fun deleteAll() {
        Log.d(TAG, "Request to delete all random texts")
        viewModelScope.launch {
            try {
                repo.deleteAll()
                Log.d(TAG, "All random texts deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete all random texts", e)
                _error.emit("Failed to delete all: ${e.message}")
            }
        }
    }

    fun delete(randomText: RandomText) {
        Log.d(TAG, "Request to delete random text: $randomText")
        viewModelScope.launch {
            try {
                repo.delete(randomText)
                Log.d(TAG, "Random text deleted successfully: $randomText")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete random text: $randomText", e)
                _error.emit("Failed to delete: ${e.message}")
            }
        }
    }
}
