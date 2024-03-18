package com.example.miniamazon.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class HomeViewPagerAdapter(
    private val fragments: List<Fragment>,
    fn: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fn, lifeCycle) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment =
        fragments[position]

}