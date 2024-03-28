package com.example.miniamazon.ui.fragments.home.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.R
import com.example.miniamazon.databinding.FragmentBaseBinding
import com.example.miniamazon.ui.adapter.NewDealsAdapter
import com.example.miniamazon.ui.adapter.OfferProductsAdapter
import com.example.miniamazon.util.visibleNavigation

open class BaseCategoryFragment : Fragment(R.layout.fragment_base) {
    private lateinit var binding: FragmentBaseBinding
    protected val productsWithOfferAdapter: OfferProductsAdapter by lazy { OfferProductsAdapter() }
    protected val productsAdapter: NewDealsAdapter by lazy { NewDealsAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseBinding
            .inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAllAdapters()

        productsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController()
                .navigate(
                    R.id.action_homeFragment_to_productDetailsFragment,
                    bundle
                )
        }
        productsWithOfferAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController()
                .navigate(
                    R.id.action_homeFragment_to_productDetailsFragment,
                    bundle
                )
        }

        binding.recyclerOffers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBase.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onAllProductsRequest()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        visibleNavigation()
    }

    fun hideOfferLoading() {
        binding.offersLoading.visibility = View.GONE
    }

    fun hideProductsLoading() {
        binding.productsLoading.visibility = View.GONE
    }

    fun showOfferLoading() {
        binding.offersLoading.visibility = View.VISIBLE
    }

    fun showProductsLoading() {
        binding.productsLoading.visibility = View.VISIBLE
    }


    open fun onOfferPagingRequest() {

    }

    open fun onAllProductsRequest() {

    }

    private fun initAllAdapters() {
        binding.apply {
            recyclerOffers.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productsWithOfferAdapter
            }
            allProductsRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(false)
                adapter = productsAdapter
            }
        }
    }
}