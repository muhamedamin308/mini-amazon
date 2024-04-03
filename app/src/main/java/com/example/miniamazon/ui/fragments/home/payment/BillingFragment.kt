package com.example.miniamazon.ui.fragments.home.payment

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.R
import com.example.miniamazon.data.classes.Address
import com.example.miniamazon.data.classes.Cart
import com.example.miniamazon.data.classes.Order
import com.example.miniamazon.data.classes.OrderStatus
import com.example.miniamazon.databinding.FragmentBillingBinding
import com.example.miniamazon.ui.adapter.AddressAdapter
import com.example.miniamazon.ui.adapter.CartBillingAdapter
import com.example.miniamazon.ui.dialog.showAlertDialog
import com.example.miniamazon.ui.viewmodel.BillingViewModel
import com.example.miniamazon.ui.viewmodel.OrderViewModel
import com.example.miniamazon.util.HorizontalItemDecoration
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.VerticalItemDecoration
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.hide
import com.example.miniamazon.util.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter(this.requireContext()) }
    private val cartBillingAdapter by lazy { CartBillingAdapter() }
    private val args by navArgs<BillingFragmentArgs>()
    private var cartProducts = emptyList<Cart>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null
    private val billingViewModel by viewModels<BillingViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartProducts = args.cart.toList()
        totalPrice = args.totalPrice
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclers()
        lifecycleScope.launchWhenStarted {
            billingViewModel.addresses.collectLatest {
                when (it) {
                    is Status.Error -> {
                        binding.addressProgressBar.gone()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> binding.addressProgressBar.show()
                    is Status.Success -> {
                        binding.addressProgressBar.gone()
                        addressAdapter.differ.submitList(it.data)
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            billingViewModel.billingCart.collectLatest {
                when (it) {
                    is Status.Error -> binding.cartProgressBar.hide()
                    is Status.Loading -> binding.cartProgressBar.show()
                    is Status.Success -> {
                        binding.cartProgressBar.hide()
                        cartBillingAdapter.differ.submitList(it.data)
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                binding.apply {
                    when (it) {
                        is Status.Error -> {
                            placeOrderBtn.revertAnimation()
                            Snackbar.make(
                                requireView(),
                                "Error: ${it.message.toString()}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        is Status.Loading -> placeOrderBtn.startAnimation()
                        is Status.Success -> {
                            placeOrderBtn.revertAnimation()
                            findNavController().navigateUp()
                            Snackbar.make(
                                requireView(),
                                "Your Order was placed!!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is Status.UnSpecified -> Unit
                    }
                }
            }
        }
        binding.addNewLocation.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }
        cartBillingAdapter.differ.submitList(cartProducts)
        binding.totalPriceTv.text = "$ $totalPrice"
        binding.exit.setOnClickListener { findNavController().navigateUp() }
        addressAdapter.onClick = {
            selectedAddress = it
        }
        binding.placeOrderBtn.setOnClickListener {
            if (selectedAddress == null) {
                Snackbar.make(
                    requireView(),
                    "Please select an address",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
        requireContext().showAlertDialog(
            title = "Order Confirmation",
            message = "Do you want to order this cart?",
            positiveButtonTitle = "Order",
            negativeButtonTitle = "Cancel",
            positiveAction = {
                val order = Order(
                    orderStatus = OrderStatus.Ordered.status,
                    totalPrice,
                    cartProducts,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
            }
        )
    }

    private fun setUpRecyclers() {
        binding.apply {
            myAddressRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                adapter = addressAdapter
                addItemDecoration(HorizontalItemDecoration())
            }
            myCartRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = cartBillingAdapter
                addItemDecoration(VerticalItemDecoration())
            }
        }
    }
}