package com.example.miniamazon.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.databinding.FragmentRegisterBinding
import com.example.miniamazon.ui.viewmodel.RegisterViewModel
import com.example.miniamazon.util.Constants.TAG
import com.example.miniamazon.util.RegisterValidation
import com.example.miniamazon.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            continueButton.setOnClickListener {
                val user = User(
                    fname.text.toString().trim(),
                    lname.text.toString().trim(),
                    emailEt.text.toString().trim()
                )
                val password = passwordEt.text.toString()
                viewModel.createNewAccountUsingEmailAndPassword(user, password)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.continueButton.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.i(TAG, it.data.toString())
                        binding.continueButton.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.continueButton.revertAnimation()
                    }

                    is Resource.UnSpecified -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect {validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.emailEt.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.passwordEt.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}