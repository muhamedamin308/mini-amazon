package com.example.miniamazon.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.AmazonApplication
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.util.Constants.PROFILE_STORAGE_PATH
import com.example.miniamazon.util.Constants.USER_COLLECTION
import com.example.miniamazon.util.RegisterValidation
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {
    private val mProfile = MutableStateFlow<Status<User>>(Status.UnSpecified())
    val profile = mProfile.asStateFlow()
    private val mProfileEdit = MutableStateFlow<Status<User>>(Status.UnSpecified())
    val profileEdit = mProfileEdit.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch { mProfile.emit(Status.Loading()) }
        fireStore.collection(USER_COLLECTION)
            .document(auth.uid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        mProfile.emit(Status.Success(user))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    mProfile.emit(Status.Error(it.message.toString()))
                }
            }
    }

    fun updateUser(user: User, userImageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success &&
                user.firstName.trim().isNotEmpty() &&
                user.lastName.trim().isNotEmpty()
        if (!areInputsValid) {
            viewModelScope.launch { mProfileEdit.emit(Status.Error("Check your input!")) }
            return
        }
        if (userImageUri == null) {
            saveUserInfo(user, true)
        } else {
            saveUserInfoWithNewImage(user, userImageUri)
        }
        viewModelScope.launch { mProfileEdit.emit(Status.Loading()) }
    }

    private fun saveUserInfoWithNewImage(user: User, profileUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitMap = MediaStore
                    .Images
                    .Media
                    .getBitmap(
                        getApplication<AmazonApplication>()
                            .contentResolver,
                        profileUri
                    )
                val byteArray = ByteArrayOutputStream()
                imageBitMap.compress(Bitmap.CompressFormat.JPEG, 93, byteArray)
                val imageByteArray = byteArray.toByteArray()
                val imageDirectory =
                    storage.child("$PROFILE_STORAGE_PATH/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInfo(user.copy(profile = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    mProfileEdit.emit(Status.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInfo(user: User, isRetrieveOldImage: Boolean) {
        fireStore.runTransaction { transaction ->
            val documentRef = fireStore.collection(USER_COLLECTION).document(auth.uid!!)
            if (isRetrieveOldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(profile = currentUser?.profile ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch { mProfileEdit.emit(Status.Success(user)) }
        }.addOnFailureListener {
            viewModelScope.launch { mProfileEdit.emit(Status.Error(it.message.toString())) }
        }
    }
}