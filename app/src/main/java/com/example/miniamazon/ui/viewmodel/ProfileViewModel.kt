package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.util.Constants
import com.example.miniamazon.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {
    private val mUser = MutableStateFlow<Status<User>>(Status.UnSpecified())
    val user = mUser.asStateFlow()

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch { mUser.emit(Status.Loading()) }
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch { mUser.emit(Status.Success(user)) }
                    }
                } else {
                    viewModelScope.launch { mUser.emit(Status.Error(error.message.toString())) }
                }
            }
    }

    fun logOutUser() {
        auth.signOut()
    }
}