package com.example.miniamazon.ui.fragments.home.process

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.miniamazon.R
import com.example.miniamazon.data.classes.Address
import com.example.miniamazon.databinding.FragmentAddressBinding
import com.example.miniamazon.ui.viewmodel.AddressViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.hide
import com.example.miniamazon.util.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment(R.layout.fragment_address) {
    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()
    private val args by navArgs<AddressFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when (it) {
                    is Status.Error -> {
                        binding.progressBar.hide()
                        Snackbar.make(
                            requireView(), "Error: ${it.message.toString()}", Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> {
                        binding.progressBar.show()
                    }

                    is Status.Success -> {
                        binding.progressBar.hide()
                        findNavController().navigateUp()
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.inValidAddress.collectLatest {
                Snackbar.make(
                    requireView(), "Error: $it", Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val address = args.address
        if (address == null) {
            binding.deleteAddressBtn.gone()
        } else {
            binding.apply {
                addressLocationHome.setText(address.homeTitle)
                fullName.setText(address.fullName)
                streetEt.setText(address.street)
                cityEt.setText(address.city)
                phoneEt.setText(address.phone)
                stateEt.setText(address.state)
            }
        }
        binding.apply {
            saveAddressBtn.setOnClickListener {
                val addressHome = addressLocationHome.text.toString()
                val fullName = fullName.text.toString()
                val street = streetEt.text.toString()
                val phone = phoneEt.text.toString()
                val city = cityEt.text.toString()
                val state = stateEt.text.toString()
                val newAddress = Address(addressHome, street, city, state, fullName, phone)
                viewModel.addAddress(newAddress)
            }
            exit.setOnClickListener { findNavController().navigateUp() }
        }
    }
}