package com.example.miniamazon.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.miniamazon.R
import com.example.miniamazon.ui.activites.home.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.invisibleNavigation() {
    val navigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    navigationView.visibility = View.GONE
}

fun Fragment.visibleNavigation() {
    val navigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    navigationView.visibility = View.VISIBLE
}