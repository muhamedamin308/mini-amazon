package com.example.miniamazon.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miniamazon.R
import com.example.miniamazon.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startTv.setOnClickListener {
            findNavController().navigate(R.id.action_introductionFragment2_to_accountsFragment2)
        }
    }
}