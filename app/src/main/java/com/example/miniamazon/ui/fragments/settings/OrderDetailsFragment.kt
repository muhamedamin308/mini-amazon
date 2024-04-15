package com.example.miniamazon.ui.fragments.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.data.classes.Order
import com.example.miniamazon.data.classes.OrderController
import com.example.miniamazon.data.classes.OrderStatus
import com.example.miniamazon.databinding.FragmentOrderDetailBinding
import com.example.miniamazon.ui.adapter.CartBillingAdapter
import com.example.miniamazon.util.VerticalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingCartAdapter by lazy { CartBillingAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()
    private lateinit var order: Order
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        order = args.order
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecycler()
        binding.apply {
            tvOrderId.text = "#${order.orderId}"
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )
            val currentOrderState = when (OrderController.getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            stepView.go(currentOrderState, true)
            if (currentOrderState == 3) {
                stepView.done(true)
            }
            tvFullName.text = order.address.fullName
            tvAddress.text =
                "${order.address.street}-${order.address.city}-${order.address.homeTitle}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "$${String.format("%.2f", order.totalPrice)}"
            exit.setOnClickListener { findNavController().navigateUp() }
        }
        billingCartAdapter.differ.submitList(order.cartProducts)
    }

    private fun setUpRecycler() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = billingCartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}