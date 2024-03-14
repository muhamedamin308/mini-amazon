package com.example.miniamazon.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.miniamazon.R
import com.example.miniamazon.databinding.FragmentIntroductionBinding
import com.example.miniamazon.ui.activites.home.ShoppingActivity
import com.example.miniamazon.ui.viewmodel.IntroductionViewModel
import com.example.miniamazon.util.Constants.Introduction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect {
                when (it) {
                    Introduction.SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    Introduction.ACCOUNT_OPTIONS -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }



        binding.imageView.setOnClickListener {
            viewModel.activateGetStarted()
            findNavController().navigate(R.id.action_introductionFragment2_to_accountsFragment2)
        }
    }
}