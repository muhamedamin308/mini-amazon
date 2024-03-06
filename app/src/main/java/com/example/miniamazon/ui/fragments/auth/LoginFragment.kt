package com.example.miniamazon.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.miniamazon.R
import com.example.miniamazon.databinding.FragmentLoginBinding
import com.example.miniamazon.ui.activites.home.ShoppingActivity
import com.example.miniamazon.ui.viewmodel.LoginViewModel
import com.example.miniamazon.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            continueButton.setOnClickListener {
                val email = emailEt.text.toString().trim()
                val password = passwordEt.text.toString()
                viewModel.loginWithEmailAndPassword(email, password)
            }
            registerTv.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment2_to_registerFragment2)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {

                    is Resource.Loading -> binding.continueButton.startAnimation()
                    is Resource.Success -> {
                        binding.continueButton.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        binding.continueButton.revertAnimation()
                        Toast.makeText(
                            requireContext(),
                            it.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Resource.UnSpecified -> Unit
                }
            }
        }
    }
}