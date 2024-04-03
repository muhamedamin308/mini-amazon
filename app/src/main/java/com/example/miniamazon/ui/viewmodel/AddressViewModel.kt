package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Address
import com.example.miniamazon.util.Constants.ADDRESS_COLLECTION
import com.example.miniamazon.util.Constants.USER_COLLECTION
import com.example.miniamazon.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val mAddress = MutableStateFlow<Status<Address>>(Status.UnSpecified())
    val address = mAddress.asStateFlow()
    private val mInvalidAddress = MutableSharedFlow<String>()
    val inValidAddress = mInvalidAddress.asSharedFlow()
    fun addAddress(address: Address) {
        val validInputs = validateInputs(address)
        if (validInputs) {
            viewModelScope.launch { mAddress.emit(Status.Loading()) }
            fireStore.collection(USER_COLLECTION)
                .document(auth.uid!!)
                .collection(ADDRESS_COLLECTION)
                .document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { mAddress.emit(Status.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { mAddress.emit(Status.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch {
                mInvalidAddress.emit("Please fill all the empty fields!")
            }
        }
    }

    private fun validateInputs(address: Address): Boolean =
        address.homeTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
}