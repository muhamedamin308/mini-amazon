package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Category
import com.example.miniamazon.data.classes.Product
import com.example.miniamazon.util.Constants.CATEGORY_COLLECTION
import com.example.miniamazon.util.Constants.IS_OFFER_EXIST
import com.example.miniamazon.util.Constants.PRODUCTS_COLLECTION
import com.example.miniamazon.util.Status
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val fireFireStore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {
    private val mOfferProducts =
        MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val offerProducts = mOfferProducts.asStateFlow()

    private val mAllProducts =
        MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val allProducts = mAllProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchAllProducts()
    }
    private fun fetchOfferProducts() {
        viewModelScope.launch {
            mOfferProducts.emit(Status.Loading())
        }
        fireFireStore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo(CATEGORY_COLLECTION, category.category)
            .whereNotEqualTo(IS_OFFER_EXIST, null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    mOfferProducts.emit(Status.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    mOfferProducts.emit(Status.Error(it.message.toString()))
                }
            }
    }

    private fun fetchAllProducts() {
        viewModelScope.launch {
            mAllProducts.emit(Status.Loading())
        }
        fireFireStore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo(CATEGORY_COLLECTION, category.category)
            .whereEqualTo(IS_OFFER_EXIST, null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    mAllProducts.emit(Status.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    mAllProducts.emit(Status.Error(it.message.toString()))
                }
            }
    }
}