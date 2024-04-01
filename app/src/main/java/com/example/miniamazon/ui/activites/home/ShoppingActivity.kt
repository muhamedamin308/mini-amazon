package com.example.miniamazon.ui.activites.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.miniamazon.R
import com.example.miniamazon.databinding.ActivityShoppingBinding
import com.example.miniamazon.ui.viewmodel.CartViewModel
import com.example.miniamazon.util.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    val viewModel by viewModels<CartViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView2)
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewModel.cart.collectLatest {
                when (it) {
                    is Status.Success -> {
                        val amount = it.data?.size ?: 0
                        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        bottomNav.getOrCreateBadge(R.id.cartFragment).apply {
                            number = amount
                            backgroundColor = resources.getColor(R.color.cancel)
                            badgeTextColor = resources.getColor(R.color.main_background)
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}