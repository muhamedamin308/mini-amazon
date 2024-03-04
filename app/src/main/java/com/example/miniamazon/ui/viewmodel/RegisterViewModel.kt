package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.util.RegisterFieldState
import com.example.miniamazon.util.RegisterValidation
import com.example.miniamazon.util.Resource
import com.example.miniamazon.util.validateEmail
import com.example.miniamazon.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authenticationFirebase: FirebaseAuth
) : ViewModel() {
    private val privateRegister =
        MutableStateFlow<Resource<FirebaseUser>>(Resource.UnSpecified())
    val register: Flow<Resource<FirebaseUser>> = privateRegister
    private val privateValidation = Channel<RegisterFieldState>()
    val validation = privateValidation.receiveAsFlow()
    fun createNewAccountUsingEmailAndPassword(user: User, password: String) {
        val userValidation = validateUser(user, password)
        if (userValidation) {
            runBlocking { privateRegister.emit(Resource.Loading()) }
            authenticationFirebase.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let { firebaseUser ->
                        privateRegister.value = Resource.Success(firebaseUser)
                    }
                }
                .addOnFailureListener {
                    privateRegister.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFailedState = RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking { privateValidation.send(registerFailedState) }
        }
    }

    private fun validateUser(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidate = validatePassword(password)
        return emailValidation is RegisterValidation.Success &&
                passwordValidate is RegisterValidation.Success
    }
}