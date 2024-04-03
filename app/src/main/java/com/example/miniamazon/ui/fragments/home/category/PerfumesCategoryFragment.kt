package com.example.miniamazon.ui.fragments.home.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.miniamazon.data.classes.Category
import com.example.miniamazon.ui.viewmodel.CategoriesViewModel
import com.example.miniamazon.ui.viewmodel.factory.BaseCategoryViewModelFactory
import com.example.miniamazon.util.Status
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class PerfumesCategoryFragment : BaseCategoryFragment() {
    @Inject
    lateinit var fireStore: FirebaseFirestore

    private val viewModel by viewModels<CategoriesViewModel> {
        BaseCategoryViewModelFactory(fireStore, Category.Perfumes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Status.Error -> {
                        hideOfferLoading()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> showOfferLoading()
                    is Status.Success -> {
                        hideOfferLoading()
                        productsWithOfferAdapter.differ.submitList(it.data)
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.allProducts.collectLatest {
                when (it) {
                    is Status.Error -> {
                        hideProductsLoading()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> showProductsLoading()
                    is Status.Success -> {
                        hideProductsLoading()
                        productsAdapter.differ.submitList(it.data)
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
    }
}