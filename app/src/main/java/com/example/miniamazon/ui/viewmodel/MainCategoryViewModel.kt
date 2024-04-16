package com.example.miniamazon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniamazon.data.classes.Product
import com.example.miniamazon.data.helper.PagingInfoHelper
import com.example.miniamazon.util.Constants
import com.example.miniamazon.util.Constants.Categories.NEW_DEALS
import com.example.miniamazon.util.Constants.Categories.SPECIAL_PRODUCTS
import com.example.miniamazon.util.Status
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {
    private val mSpecialProduct = MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val specialProduct = mSpecialProduct.asStateFlow()

    private val mRecommendedProduct = MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val recommendedProduct = mRecommendedProduct.asStateFlow()

    private val mNewDeals = MutableStateFlow<Status<List<Product>>>(Status.UnSpecified())
    val newDeals = mNewDeals.asStateFlow()

    private val paging = PagingInfoHelper()

    init {
        fetchSpecialProduct()
        fetchRecommendedProducts()
        fetchNewDeals()
    }

    private fun fetchSpecialProduct() {
        viewModelScope.launch {
            mSpecialProduct.emit(Status.Loading())
        }
        fireStore.collection(Constants.Collections.PRODUCTS_COLLECTION)
            .whereEqualTo("category", SPECIAL_PRODUCTS).get().addOnSuccessListener {
                val specialProducts = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    mSpecialProduct.emit(Status.Success(specialProducts))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    mSpecialProduct.emit(Status.Error(it.message.toString()))
                }
            }
    }

    fun fetchRecommendedProducts() {
        if (!paging.isPagingEnd) {
            viewModelScope.launch {
                mRecommendedProduct.emit(Status.Loading())
            }
            fireStore.collection(Constants.Collections.PRODUCTS_COLLECTION)
                .limit(paging.viewPosition * 10).get().addOnSuccessListener {
                    val recommendedProducts = it.toObjects(Product::class.java)
                    paging.isPagingEnd = recommendedProducts == paging.oldProducts
                    paging.oldProducts = recommendedProducts
                    viewModelScope.launch {
                        mRecommendedProduct.emit(Status.Success(recommendedProducts))
                    }
                    paging.viewPosition++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        mRecommendedProduct.emit(Status.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun fetchNewDeals() {
        viewModelScope.launch {
            mNewDeals.emit(Status.Loading())
        }
        fireStore.collection(Constants.Collections.PRODUCTS_COLLECTION)
            .whereEqualTo("category", NEW_DEALS).get().addOnSuccessListener {
                val newDeals = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    mNewDeals.emit(Status.Success(newDeals))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    mNewDeals.emit(Status.Error(it.message.toString()))
                }
            }
    }
}