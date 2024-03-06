package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.util.Resource
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
    private val privateLogin = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = privateLogin.asSharedFlow()
    private val privateResetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = privateResetPassword.asSharedFlow()
    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            privateLogin.emit(Resource.Loading())
        }
        authenticationFirebase.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it?.user?.let {
                        privateLogin.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    privateLogin.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            privateResetPassword.emit(Resource.Loading())
        }
        authenticationFirebase
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    privateResetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    privateResetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}