package com.example.miniamazon.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miniamazon.data.classes.Category
import com.example.miniamazon.ui.viewmodel.CategoriesViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("UNCHECKED_CAST")
class BaseCategoryViewModelFactory(
    private val firebaseFireStore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CategoriesViewModel(firebaseFireStore, category) as T
}