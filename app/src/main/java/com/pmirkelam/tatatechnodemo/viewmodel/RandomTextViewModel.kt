package com.pmirkelam.tatatechnodemo.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pmirkelam.tatatechnodemo.data.repo.RandomTextRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RandomTextViewModel(
    private val repo: RandomTextRepository
) : ViewModel() {
    val texts = repo.allTexts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetch(maxLength: Int = 12) {
        viewModelScope.launch {
            repo.fetchAndSave(maxLength)
        }
    }
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