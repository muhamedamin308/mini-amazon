package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.util.Constants
import com.example.miniamazon.util.RegisterFieldState
import com.example.miniamazon.util.RegisterValidation
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.validateEmail
import com.example.miniamazon.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authenticationFirebase: FirebaseAuth, private val fireStore: FirebaseFirestore
) : ViewModel() {
    private val mRegister = MutableStateFlow<Status<User>>(Status.UnSpecified())
    val register: Flow<Status<User>> = mRegister
    private val mValidation = Channel<RegisterFieldState>()
    val validation = mValidation.receiveAsFlow()
    fun createNewAccount(newUser: User, password: String) {
        val userValidation = validateUser(newUser, password)
        if (userValidation) {
            runBlocking { mRegister.emit(Status.Loading()) }
            authenticationFirebase.createUserWithEmailAndPassword(newUser.email, password)
                .addOnSuccessListener {
                    it.user?.let { firebaseUser ->
                        saveUserDetails(firebaseUser.uid, newUser)
                    }
                }.addOnFailureListener {
                    mRegister.value = Status.Error(it.message.toString())
                }
        } else {
            val registerFailedState = RegisterFieldState(
                validateEmail(newUser.email), validatePassword(password)
            )
            runBlocking { mValidation.send(registerFailedState) }
        }
    }

    private fun saveUserDetails(userId: String, user: User) {
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(userId).set(user)
            .addOnSuccessListener {
                mRegister.value = Status.Success(user)
            }.addOnFailureListener {
                mRegister.value = Status.Error(it.message.toString())
            }
    }

    private fun validateUser(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidate = validatePassword(password)
        return emailValidation is RegisterValidation.Success && passwordValidate is RegisterValidation.Success
    }
}