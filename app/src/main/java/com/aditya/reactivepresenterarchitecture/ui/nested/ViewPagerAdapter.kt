package com.aditya.reactivepresenterarchitecture.ui.nested

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.NestedDetailFragment
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.list.NestedListFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NestedListFragment.newInstance()
            1 -> NestedDetailFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

}