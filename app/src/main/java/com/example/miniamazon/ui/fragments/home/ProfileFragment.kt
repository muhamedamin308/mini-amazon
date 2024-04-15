package com.example.miniamazon.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.miniamazon.R
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.databinding.FragmentProfileBinding
import com.example.miniamazon.ui.activites.auth.AuthenticationActivity
import com.example.miniamazon.ui.viewmodel.ProfileViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.show
import com.example.miniamazon.util.visibleNavigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import firebase.com.protolitewrapper.BuildConfig
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
        binding.allOrdersView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderFragment)
        }
        binding.billingCartView.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f, emptyArray(), false)
            findNavController().navigate(action)
        }
        binding.logOutUser.setOnClickListener {
            viewModel.logOutUser()
            val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.tvAppVersion.text = "Version ${BuildConfig.VERSION_CODE}"
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it) {
                    is Status.Error -> {
                        binding.profileProgressBar.gone()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is Status.Loading -> binding.profileProgressBar.show()
                    is Status.Success -> {
                        binding.profileProgressBar.gone()
                        Glide.with(requireView())
                            .load(it.data!!.profile)
                            .error(ColorDrawable(Color.BLACK))
                            .into(binding.userProfilePicture)
                        binding.userFullName.text = "${it.data.firstName} ${it.data.lastName}"
                        binding.userEmail.text = it.data.email
                    }
                    is Status.UnSpecified -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        visibleNavigation()
    }
}