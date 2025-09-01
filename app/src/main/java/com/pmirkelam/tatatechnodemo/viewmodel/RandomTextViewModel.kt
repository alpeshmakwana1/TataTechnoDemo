package com.pmirkelam.tatatechnodemo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pmirkelam.tatatechnodemo.data.model.RandomText
import com.pmirkelam.tatatechnodemo.data.repo.RandomTextRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RandomTextViewModel(
    private val repo: RandomTextRepository
) : ViewModel() {
    val allTexts: StateFlow<List<RandomText>> =
        repo.getAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun generateRandom(length: Int) {
        viewModelScope.launch {
            val result = repo.generateRandom(length)
            if (result.isFailure) {
                _error.emit("Failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteAll() = viewModelScope.launch { repo.deleteAll() }
    fun delete(randomText: RandomText) = viewModelScope.launch { repo.delete(randomText) }
}

class RandomTextViewModelFactory(
    private val repo: RandomTextRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RandomTextViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RandomTextViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}