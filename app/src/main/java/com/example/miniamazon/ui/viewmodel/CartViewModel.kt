package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Cart
import com.example.miniamazon.data.firebase.FirebaseDataLayer
import com.example.miniamazon.data.helper.QuantityChangeHelper
import com.example.miniamazon.data.helper.getProductPrice
import com.example.miniamazon.util.Constants
import com.example.miniamazon.util.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dataLayer: FirebaseDataLayer
) : ViewModel() {
    private val mCart = MutableStateFlow<Status<List<Cart>>>(Status.UnSpecified())
    val cart = mCart.asStateFlow()

    private val mDeleteDialog = MutableSharedFlow<Cart>()
    val deleteDialog = mDeleteDialog.asSharedFlow()

    private var cartDocument = emptyList<DocumentSnapshot>()
    val totalPrice = cart.map {
        when (it) {
            is Status.Success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }

    init {
        getCartProduct()
    }

    private fun getCartProduct() {
        viewModelScope.launch { mCart.emit(Status.Loading()) }
        fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.Collections.CART_COLLECTION).addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        mCart.emit(Status.Error(error?.message.toString()))
                    }
                } else {
                    cartDocument = value.documents
                    val cartList = value.toObjects(Cart::class.java)
                    viewModelScope.launch {
                        mCart.emit(Status.Success(cartList))
                    }
                }
            }
    }

    fun changeQuantity(
        cartProduct: Cart, quantityChangeHelper: QuantityChangeHelper
    ) {
        val index = cart.value.data?.indexOf(cartProduct)
        /**
         * index could be = -1 if the function [getCartProduct] delays which will also delay the result to be inside the [mCart]
         * and to prevent the app from crashing we make check
         */
        if (index != null && index != -1) {
            val documentId = cartDocument[index].id
            when (quantityChangeHelper) {
                QuantityChangeHelper.INCREASE -> {
                    viewModelScope.launch { mCart.emit(Status.Loading()) }
                    increaseQuantity(documentId)
                }

                QuantityChangeHelper.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { mDeleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { mCart.emit(Status.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    fun deleteProduct(product: Cart) {
        val index = cart.value.data?.indexOf(product)
        if (index != null && index != -1) {
            val documentId = cartDocument[index].id
            fireStore.collection(Constants.Collections.USER_COLLECTION).document(auth.uid!!)
                .collection(Constants.Collections.CART_COLLECTION).document(documentId).delete()
        }
    }

    private fun increaseQuantity(documentId: String) {
        dataLayer.increaseExcitingProductQuantity(documentId) { _, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    mCart.emit(Status.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        dataLayer.decreaseExcitingProductQuantity(documentId) { _, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    mCart.emit(Status.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun calculatePrice(data: List<Cart>): Float {
        return data.sumOf { product ->
            val price = product.products.offerPercentage.getProductPrice(product.products.price)
            if (price != null) {
                (price * product.quantity).toDouble()
            } else {
                (product.products.price * product.quantity).toDouble()
            }
        }.toFloat()
    }
}