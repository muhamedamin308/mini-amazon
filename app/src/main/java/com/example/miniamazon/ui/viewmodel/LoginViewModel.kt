package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationFirebase: FirebaseAuth
) : ViewModel() {
    private val privateLogin = MutableSharedFlow<Status<FirebaseUser>>()
    val login = privateLogin.asSharedFlow()
    private val privateResetPassword = MutableSharedFlow<Status<String>>()
    val resetPassword = privateResetPassword.asSharedFlow()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            privateLogin.emit(Status.Loading())
        }
        authenticationFirebase
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it?.user?.let {
                        privateLogin.emit(Status.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    privateLogin.emit(Status.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            privateResetPassword.emit(Status.Loading())
        }
        authenticationFirebase
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    privateResetPassword.emit(Status.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    privateResetPassword.emit(Status.Error(it.message.toString()))
                }
            }
    }
}