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
class OrdersHistoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {
    private val mOrders = MutableStateFlow<Status<List<Order>>>(Status.UnSpecified())
    val orders = mOrders.asStateFlow()

    init {
        getAllOrders()
    }

    private fun getAllOrders() {
        viewModelScope.launch { mOrders.emit(Status.Loading()) }
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.Collections.ORDER_COLLECTION).get().addOnSuccessListener {
                val allOrders = it.toObjects(Order::class.java)
                viewModelScope.launch { mOrders.emit(Status.Success(allOrders)) }
            }.addOnFailureListener {
                viewModelScope.launch { mOrders.emit(Status.Error(it.message.toString())) }
            }
    }
}