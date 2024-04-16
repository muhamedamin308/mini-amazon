package com.example.miniamazon.ui.fragments.home

import android.annotation.SuppressLint
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
import com.example.miniamazon.ui.dialog.showAlertDialog
import com.example.miniamazon.ui.viewmodel.CartViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.VerticalItemDecoration
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.show
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { MyCartAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()
    private var totalPrice = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
                    totalPrice = it
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
                            requireView(), "Error: ${it.message.toString()}", Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> {
                        binding.progressBar.show()
                    }

                    is Status.Success -> {
                        binding.progressBar.hide()
                        if (it.data!!.isEmpty()) cartEmptySetUp()
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
                requireContext().showAlertDialog(title = "Delete item from cart",
                    message = "Do you want to delete this item from cart?",
                    positiveButtonTitle = "Delete",
                    negativeButtonTitle = "Cancel",
                    positiveAction = {
                        viewModel.deleteProduct(it)
                    })
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
        binding.checkoutButton.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                totalPrice,
                cartAdapter.differ.currentList.toTypedArray(),
                true
            )
            findNavController().navigate(action)
        }
    }

    private fun setUpCart() {
        binding.apply {
            clEmptyCart.gone()
            nestedRecycler.show()
            linearLayout2.show()
        }
    }

    private fun cartEmptySetUp() {
        binding.apply {
            clEmptyCart.show()
            nestedRecycler.gone()
            linearLayout2.gone()
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