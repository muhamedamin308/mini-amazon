package com.example.miniamazon.ui.fragments.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miniamazon.databinding.FragmentProductDetailsBinding
import com.example.miniamazon.ui.adapter.ProductColorAdapter
import com.example.miniamazon.ui.adapter.ProductDetailsViewPagerAdapter
import com.example.miniamazon.ui.adapter.ProductSizesAdapter
import com.example.miniamazon.util.invisibleNavigation

class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding

    private val productImagesAdapter by
    lazy { ProductDetailsViewPagerAdapter() }

    private val productColorAdapter by
    lazy { ProductColorAdapter() }

    private val productSizeAdapter by
    lazy { ProductSizesAdapter(this.requireContext()) }

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product
        initRecyclers()
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
                productPrice.visibility = View.GONE
                productOldPrice.alpha = 1f
                productOldPrice.textSize = 22f
            }
            productOldPrice.text = "$ ${product.price}"
            productDetailsBack.setOnClickListener { findNavController().navigateUp() }
        }
        productImagesAdapter.differ.submitList(product.images)
        product.colors?.let { productColorAdapter.differ.submitList(it) }
        product.sizes?.let { productSizeAdapter.differ.submitList(it) }
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
}