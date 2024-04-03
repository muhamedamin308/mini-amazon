package com.example.miniamazon.ui.fragments.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miniamazon.R
import com.example.miniamazon.data.classes.Cart
import com.example.miniamazon.data.classes.Product
import com.example.miniamazon.databinding.FragmentProductDetailsBinding
import com.example.miniamazon.ui.adapter.ProductColorAdapter
import com.example.miniamazon.ui.adapter.ProductDetailsViewPagerAdapter
import com.example.miniamazon.ui.adapter.ProductSizesAdapter
import com.example.miniamazon.ui.viewmodel.ProductDetailsViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.hide
import com.example.miniamazon.util.invisibleNavigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding

    private val productImagesAdapter by
    lazy { ProductDetailsViewPagerAdapter() }

    private val productColorAdapter by
    lazy { ProductColorAdapter() }

    private val productSizeAdapter by
    lazy { ProductSizesAdapter(this.requireContext()) }

    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<ProductDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        invisibleNavigation()
        binding = FragmentProductDetailsBinding
            .inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product
        initRecyclers()
        fillProductUIDetails(product)
        productImagesAdapter.differ.submitList(product.images)
        product.colors?.let { productColorAdapter.differ.submitList(it) }
        product.sizes?.let { productSizeAdapter.differ.submitList(it) }
        productSizeAdapter.onItemClicked = {
            selectedSize = it
        }
        productColorAdapter.onItemClicked = {
            selectedColor = it
        }
        binding.addToCartButton.setOnClickListener {
            viewModel.addProductToCart(Cart(1, selectedColor, selectedSize, product))
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Status.Error -> {
                        binding.addToCartButton.revertAnimation()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> binding.addToCartButton.startAnimation()
                    is Status.Success -> {
                        binding.addToCartButton.revertAnimation()
                        binding.addToCartButton.setBackgroundColor(resources.getColor(R.color.Unselected_category))
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
    }

    private fun initRecyclers() {
        binding.apply {
            viewpagerProductImage.adapter = productImagesAdapter
            recyclerSizes.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productSizeAdapter
            }
            recyclerColors.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productColorAdapter
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillProductUIDetails(product: Product) {
        binding.apply {
            productName.text = product.name
            productDescription.text = product.description
            product.offerPercentage?.let {
                var newPrice = 1f - it
                newPrice *= product.price
                productPrice.text = "$ ${String.format("%.2f", newPrice)}"
                productOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            if (product.offerPercentage == null) {
                productPrice.gone()
                productOldPrice.alpha = 1f
                productOldPrice.textSize = 22f
            }
            productOldPrice.text = "$ ${product.price}"
            productDetailsBack.setOnClickListener { findNavController().navigateUp() }
            if (product.colors.isNullOrEmpty())
                colorsTv.hide()
            if (product.sizes.isNullOrEmpty())
                sizesTv.hide()
        }
    }
}