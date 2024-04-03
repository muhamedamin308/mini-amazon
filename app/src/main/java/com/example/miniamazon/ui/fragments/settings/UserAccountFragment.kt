package com.example.miniamazon.ui.fragments.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.miniamazon.data.classes.User
import com.example.miniamazon.databinding.FragmentUserAccountBinding
import com.example.miniamazon.ui.dialog.setUpBottomSheetDialog
import com.example.miniamazon.ui.viewmodel.LoginViewModel
import com.example.miniamazon.ui.viewmodel.UserAccountViewModel
import com.example.miniamazon.util.Status
import com.example.miniamazon.util.gone
import com.example.miniamazon.util.hide
import com.example.miniamazon.util.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private val authViewModel by viewModels<LoginViewModel>()
    private var userProfileImageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                userProfileImageUri = it?.data?.data
                Glide.with(this)
                    .load(userProfileImageUri)
                    .into(binding.userImageProfile)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.profile.collectLatest {
                when (it) {
                    is Status.Error -> {
                        showUserAfterLoading()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> hideUserWhileLoading()
                    is Status.Success -> {
                        showUserAfterLoading()
                        showUserInformation(it.data!!)
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.profileEdit.collectLatest {
                when (it) {
                    is Status.Error -> {
                        binding.continueButton.revertAnimation()
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Loading -> binding.continueButton.startAnimation()
                    is Status.Success -> {
                        binding.continueButton.revertAnimation()
                        findNavController().navigateUp()
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            authViewModel.resetPassword.collect {
                when (it) {

                    is Status.Loading -> Unit
                    is Status.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Reset link was send to your email.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.Error -> {
                        Snackbar.make(
                            requireView(),
                            "Error: ${it.message.toString()}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Status.UnSpecified -> Unit
                }
            }
        }
        binding.continueButton.setOnClickListener {
            binding.apply {
                val names = fullName.text.toString().split(" ")
                val firstName = names[0].trim()
                val lastName = names[1].trim()
                val email = emailEt.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, userProfileImageUri)
            }
        }
        binding.userImageProfileEdit.setOnClickListener {
            binding.apply {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                imageActivityResultLauncher.launch(intent)
            }
        }
        binding.changeForgetPassword.setOnClickListener {
            setUpBottomSheetDialog {
                authViewModel.resetPassword(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUserInformation(user: User) {
        binding.apply {
            Glide.with(requireContext())
                .load(user.profile)
                .error(ColorDrawable(Color.BLACK))
                .into(userImageProfile)
            fullName.setText("${user.firstName} ${user.lastName}")
            emailEt.setText(user.email)
        }
    }

    private fun showUserAfterLoading() {
        binding.apply {
            accountProgressBar.gone()
            userImageProfile.show()
            fullName.show()
            emailEt.show()
            userImageProfileEdit.show()
            continueButton.show()
            changeForgetPassword.show()
        }
    }

    private fun hideUserWhileLoading() {
        binding.apply {
            accountProgressBar.show()
            userImageProfile.hide()
            fullName.hide()
            emailEt.hide()
            userImageProfileEdit.hide()
            continueButton.hide()
            changeForgetPassword.hide()
        }
    }
}