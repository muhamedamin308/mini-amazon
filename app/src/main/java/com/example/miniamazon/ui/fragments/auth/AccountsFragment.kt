package com.example.miniamazon.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miniamazon.R
import com.example.miniamazon.databinding.FragmentAccountsBinding
import com.example.miniamazon.databinding.FragmentIntroductionBinding

class AccountsFragment: Fragment() {
    private lateinit var binding: FragmentAccountsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            registerButton.setOnClickListener {
                findNavController().navigate(R.id.action_accountsFragment2_to_registerFragment2)
            }
            loginButton.setOnClickListener {
                findNavController().navigate(R.id.action_accountsFragment2_to_loginFragment2)
            }
        }
    }
}