package com.example.miniamazon.ui.fragments.home.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.miniamazon.R
import com.example.miniamazon.data.adapter.NewDealsAdapter
import com.example.miniamazon.data.adapter.RecommendedProductsAdapter
import com.example.miniamazon.data.adapter.SpecialProductsAdapter
import com.example.miniamazon.databinding.FragmentMainCategoryBinding
import com.example.miniamazon.ui.viewmodel.MainCategoryViewModel
import com.example.miniamazon.util.Constants.TAG
import com.example.miniamazon.util.GridSpaceItemDecoration
import com.example.miniamazon.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductAdapter: SpecialProductsAdapter
    private lateinit var recommendedProductsAdapter: RecommendedProductsAdapter
    private lateinit var newDealsAdapter: NewDealsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpecialRecyclerView()
        initNewDealsRecyclerView()
        initRecommendedRecyclerView()
        lifecycleScope.launchWhenStarted {
            viewModel.apply {
                specialProduct.collectLatest { status ->
                    when (status) {
                        is Status.Error -> {
                            showLoadingDialog()
                            Log.e(TAG, status.message.toString())
                            Toast.makeText(
                                requireContext(),
                                status.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Status.Loading -> {
                            showLoadingDialog()
                        }

                        is Status.Success -> {
                            specialProductAdapter.differ.submitList(status.data)
                            hideLoadingDialog()
                        }

                        is Status.UnSpecified -> Unit
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.apply {
                recommendedProduct.collectLatest { status ->
                    when (status) {
                        is Status.Error -> {
                            binding.recommendedProductsProgressBar.visibility = View.GONE
                            Log.e(TAG, status.message.toString())
                            Toast.makeText(
                                requireContext(),
                                status.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Status.Loading -> {
                            binding.recommendedProductsProgressBar.visibility = View.VISIBLE
                        }

                        is Status.Success -> {
                            recommendedProductsAdapter.differ.submitList(status.data)
                            binding.recommendedProductsProgressBar.visibility = View.GONE
                        }

                        is Status.UnSpecified -> Unit
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.apply {
                newDeals.collectLatest { status ->
                    when (status) {
                        is Status.Error -> {
                            showLoadingDialog()
                            Log.e(TAG, status.message.toString())
                            Toast.makeText(
                                requireContext(),
                                status.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Status.Loading -> {
                            showLoadingDialog()
                        }

                        is Status.Success -> {
                            newDealsAdapter.differ.submitList(status.data)
                            hideLoadingDialog()
                        }

                        is Status.UnSpecified -> Unit
                    }
                }
            }
        }

        binding.nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { view, _, scrollY, _, _ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY) {
                viewModel.fetchRecommendedProducts()
            }
        })
    }

    private fun hideLoadingDialog() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoadingDialog() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun initSpecialRecyclerView() {
        specialProductAdapter = SpecialProductsAdapter()
        binding.recSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
        }
    }

    private fun initRecommendedRecyclerView() {
        val spaceInPixel = resources.getDimensionPixelSize(R.dimen.spacing)
        recommendedProductsAdapter = RecommendedProductsAdapter()
        binding.recRecommendedProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(
                GridSpaceItemDecoration(
                    2,
                    spaceInPixel,
                    true
                )
            )
            adapter = recommendedProductsAdapter
        }
    }

    private fun initNewDealsRecyclerView() {
        newDealsAdapter = NewDealsAdapter()
        binding.recyclerNewDeals.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(false)
            adapter = newDealsAdapter
        }
    }
}