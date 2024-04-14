package com.example.miniamazon.ui.fragments.home.process

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.databinding.FragmentOrderBinding
import com.example.miniamazon.ui.adapter.OrdersHistoryAdapter
import com.example.miniamazon.ui.viewmodel.OrdersHistoryViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class OrdersHistoryFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private val viewModel by viewModels<OrdersHistoryViewModel>()
    private val ordersAdapter by lazy { OrdersHistoryAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecycler()
        lifecycleScope.launchWhenStarted {
            viewModel.orders.collectLatest {
                when (it) {
                    is Status.Error -> {
                        binding.ordersProgressBar.gone()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> {
                        binding.ordersProgressBar.show()
                    }

                    is Status.Success -> {
                        binding.ordersProgressBar.gone()
                        ordersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.clEmptyCart.show()
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpRecycler() {
        binding.orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = ordersAdapter
        }
    }
}