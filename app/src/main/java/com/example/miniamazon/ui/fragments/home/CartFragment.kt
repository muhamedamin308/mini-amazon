package com.example.miniamazon.ui.fragments.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miniamazon.R
import com.example.miniamazon.data.helper.QuantityChangeHelper
import com.example.miniamazon.databinding.FragmentCartBinding
import com.example.miniamazon.ui.adapter.MyCartAdapter
import com.example.miniamazon.ui.viewmodel.CartViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.VerticalItemDecoration
import com.example.miniamazon.util.gone
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { MyCartAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        lifecycleScope.launchWhenStarted {
            viewModel.totalPrice.collectLatest { price ->
                price?.let {
                    binding.totalPriceTv.text = "$ $price"
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.cart.collectLatest {
                when (it) {
                    is Status.Error -> {
                        binding.progressBar.gone()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> {
                        binding.progressBar.show()
                    }

                    is Status.Success -> {
                        binding.progressBar.hide()
                        if (it.data!!.isEmpty())
                            cartEmptySetUp()
                        else {
                            setUpCart()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .apply {
                        setTitle("Delete item from cart")
                        setMessage("Do you want to delete this item from cart?")
                        setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteProduct(it)
                            dialog.dismiss()
                        }
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }
                alertDialog.create()
                alertDialog.show()
            }
        }
        cartAdapter.onProductClicked = {
            val bundle = Bundle().apply { putParcelable("product", it.products) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }
        cartAdapter.onPlusClicked = {
            viewModel.changeQuantity(it, QuantityChangeHelper.INCREASE)
        }
        cartAdapter.onMinusClicked = {
            viewModel.changeQuantity(it, QuantityChangeHelper.DECREASE)
        }
    }

    private fun setUpCart() {
        binding.apply {
            clEmptyCart.visibility = View.GONE
            nestedRecycler.visibility = View.VISIBLE
            linearLayout2.visibility = View.VISIBLE
        }
    }

    private fun cartEmptySetUp() {
        binding.apply {
            clEmptyCart.visibility = View.VISIBLE
            nestedRecycler.visibility = View.GONE
            linearLayout2.visibility = View.GONE
        }
    }

    private fun initRecyclerView() {
        binding.myCartRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}