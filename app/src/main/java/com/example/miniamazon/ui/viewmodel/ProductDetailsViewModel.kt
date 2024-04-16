package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Cart
import com.example.miniamazon.data.firebase.FirebaseDataLayer
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
class ProductDetailsViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dataLayer: FirebaseDataLayer
) : ViewModel() {
    private val mAddToCart = MutableStateFlow<Status<Cart>>(Status.UnSpecified())
    val addToCart = mAddToCart.asStateFlow()

    fun addProductToCart(cart: Cart) {
        viewModelScope.launch { mAddToCart.emit(Status.Loading()) }
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.Collections.CART_COLLECTION)
            .whereEqualTo("products.id", cart.products.id).get().addOnSuccessListener {
                it.documents.let { snapshots ->
                    if (snapshots.isEmpty()) {
                        addNewProduct(cart)
                    } else {
                        val snapshotCart = snapshots.first().toObject(Cart::class.java)
                        if (snapshotCart == cart) {
                            val docId = snapshots.first().id
                            increaseExistingProduct(docId, cart)
                        } else {
                            addNewProduct(cart)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { mAddToCart.emit(Status.Error(it.message.toString())) }
            }
    }

    private fun addNewProduct(cart: Cart) {
        dataLayer.addProductToCart(cart) { cartProduct, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    mAddToCart.emit(Status.Success(cartProduct!!))
                } else {
                    mAddToCart.emit(Status.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseExistingProduct(documentId: String, cartProduct: Cart) {
        dataLayer.increaseExcitingProductQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    mAddToCart.emit(Status.Success(cartProduct))
                } else {
                    mAddToCart.emit(Status.Error(exception.message.toString()))
                }
            }
        }
    }
}