package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Product
import com.example.miniamazon.util.Constants.PRODUCTS_COLLECTION
import com.example.miniamazon.util.Status
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {
    private val specialProductState =
        MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val specialProduct: StateFlow<Status<List<Product>>> =
        specialProductState

    private val recommendedProductState =
        MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val recommendedProduct: StateFlow<Status<List<Product>>> =
        recommendedProductState

    private val newDealsState =
        MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val newDeals: StateFlow<Status<List<Product>>> =
        newDealsState

    init {
        fetchSpecialProduct()
        fetchRecommendedProducts()
        fetchNewDeals()
    }

    private fun fetchSpecialProduct() {
        viewModelScope.launch {
            specialProductState.emit(Status.Loading())
        }
        fireStore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("category", "Special Products")
            .get()
            .addOnSuccessListener {
                val specialProducts = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    specialProductState.emit(Status.Success(specialProducts))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    specialProductState.emit(Status.Error(it.message.toString()))
                }
            }
    }

    private fun fetchRecommendedProducts() {
        viewModelScope.launch {
            recommendedProductState.emit(Status.Loading())
        }
        fireStore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("category", "Recommended For You")
            .get()
            .addOnSuccessListener {
                val recommendedProducts = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    recommendedProductState.emit(Status.Success(recommendedProducts))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    recommendedProductState.emit(Status.Error(it.message.toString()))
                }
            }
    }

    private fun fetchNewDeals() {
        viewModelScope.launch {
            newDealsState.emit(Status.Loading())
        }
        fireStore.collection(PRODUCTS_COLLECTION)
            .whereEqualTo("category", "New Deals")
            .get()
            .addOnSuccessListener {
                val newDeals = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    newDealsState.emit(Status.Success(newDeals))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    newDealsState.emit(Status.Error(it.message.toString()))
                }
            }
    }
}