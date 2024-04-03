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
    private val mLogin = MutableSharedFlow<Status<FirebaseUser>>()
    val login = mLogin.asSharedFlow()
    private val mResetPassword = MutableSharedFlow<Status<String>>()
    val resetPassword = mResetPassword.asSharedFlow()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            mLogin.emit(Status.Loading())
        }
        authenticationFirebase
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it?.user?.let {
                        mLogin.emit(Status.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    mLogin.emit(Status.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            mResetPassword.emit(Status.Loading())
        }
        authenticationFirebase
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    mResetPassword.emit(Status.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    mResetPassword.emit(Status.Error(it.message.toString()))
                }
            }
    }
}