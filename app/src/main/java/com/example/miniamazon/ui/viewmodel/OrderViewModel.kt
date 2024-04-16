package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.order.Order
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
class OrderViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {
    private val mOrder = MutableStateFlow<Status<Order>>(Status.UnSpecified())
    val order = mOrder.asStateFlow()
    fun placeOrder(order: Order) {
        viewModelScope.launch { mOrder.emit(Status.Loading()) }
        fireStore.runBatch {
            // Add the order to the user order collection for user history
            fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
                .collection(Constants.Collections.ORDER_COLLECTION).document().set(order)
            // Add the order to the orders collection for admin panel
            fireStore.collection(Constants.Collections.ORDER_COLLECTION).document().set(order)
            // Delete the products from user cart
            fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
                .collection(Constants.Collections.CART_COLLECTION).get().addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        documentSnapshot.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch { mOrder.emit(Status.Success(order)) }
        }.addOnFailureListener {
            viewModelScope.launch { mOrder.emit(Status.Error(it.message.toString())) }
        }
    }
}