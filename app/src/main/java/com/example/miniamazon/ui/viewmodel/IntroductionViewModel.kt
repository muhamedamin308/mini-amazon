package com.example.miniamazon.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.util.Constants.Introduction
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val mStateFlow = MutableStateFlow(0)
    val stateFlow = mStateFlow.asStateFlow()

    init {
        val isStarted = sharedPreferences
            .getBoolean(
                Introduction.INTRODUCTION_SHARED_KEY,
                false
            )
        val user = firebaseAuth.currentUser
        if (user != null) {
            viewModelScope.launch {
                mStateFlow.emit(Introduction.SHOPPING_ACTIVITY)
            }
        } else if (isStarted) {
            viewModelScope.launch {
                mStateFlow.emit(Introduction.ACCOUNT_OPTIONS)
            }
        } else {
            Unit
        }
    }

    fun activateGetStarted() {
        sharedPreferences
            .edit()
            .putBoolean(Introduction.INTRODUCTION_SHARED_KEY, true)
            .apply()
    }
}