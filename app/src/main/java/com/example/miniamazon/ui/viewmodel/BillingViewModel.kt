package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Address
import com.example.miniamazon.data.classes.Cart
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
class BillingViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {
    private val mBillingCart = MutableStateFlow<Status<List<Cart>>>(Status.UnSpecified())
    private val mAddresses = MutableStateFlow<Status<List<Address>>>(Status.UnSpecified())
    val billingCart = mBillingCart.asStateFlow()
    val addresses = mAddresses.asStateFlow()

    init {
        fetchUserAddresses()
        fetchUserCart()
    }

    private fun fetchUserAddresses() {
        viewModelScope.launch { mAddresses.emit(Status.Loading()) }
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.Collections.ADDRESS_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { mAddresses.emit(Status.Error(error.message.toString())) }
                    return@addSnapshotListener
                } else {
                    val snapshotAddresses = value?.toObjects(Address::class.java)
                    snapshotAddresses?.let {
                        viewModelScope.launch { mAddresses.emit(Status.Success(it)) }
                    }
                }
            }
    }

    private fun fetchUserCart() {
        viewModelScope.launch { mBillingCart.emit(Status.Loading()) }
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.Collections.CART_COLLECTION).addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { mBillingCart.emit(Status.Error(error.message.toString())) }
                    return@addSnapshotListener
                } else {
                    val snapshotCarts = value?.toObjects(Cart::class.java)
                    snapshotCarts?.let {
                        viewModelScope.launch { mBillingCart.emit(Status.Success(it)) }
                    }
                }
            }

    }
}