package com.aditya.reactivepresenterarchitecture.ui.nested.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.aditya.reactivepresenterarchitecture.databinding.FragmentNestedParentBinding
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.reactive_presenter.ui.BaseReactiveFragment
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.ui.nested.NESTED_FRAGMENT_KEY
import com.aditya.reactivepresenterarchitecture.ui.nested.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class NestedParentFragment : BaseReactiveFragment<ParentPresenter>() {

    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var binding: FragmentNestedParentBinding? = null

    companion object {
        @JvmStatic
        fun newInstance() = NestedParentFragment().apply {
            arguments = Bundle().apply {
                putString(NESTED_FRAGMENT_KEY, NestedParentFragment::class.java.simpleName)
            }
        }
    }

    override fun onPresenter(): ParentPresenter = PresenterFactory.getOrCreate()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNestedParentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        binding?.let {
            viewPagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            it.viewPager.apply {
                adapter = viewPagerAdapter
                var isRegister = true
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        if (!isRegister) presenter.setCurrentPage(position)
                        else isRegister = false
                    }
                })
                offscreenPageLimit = 2
            }

            val list = listOf("List", "Detail")
            TabLayoutMediator(it.tabLayout, it.viewPager) { tab, position ->
                tab.text = list[position]
            }.attach()
        }
    }

    override fun observeState(lifecycleProvider: IRxLifecycleProvider) {
        presenter.observeViewState(lifecycleProvider) { state ->
            when (state) {
                is ParentViewState.Loading -> {
                    binding?.let {
                        it.viewPager.currentItem = state.getModelView().currentPosition
                        it.progressBarContainer.visibility =
                            if (state.getModelView().result != null) View.GONE else View.VISIBLE
                    }
                }
                is ParentViewState.Success -> {
                    binding?.let {
                        if (it.progressBarContainer.visibility == View.GONE) return@let
                        it.progressBarContainer.visibility = View.GONE
                    }
                }
                is ParentViewState.Error -> {
                    if (state.getModelView().result != null) return@observeViewState
                    binding?.progressBarContainer?.visibility = View.GONE
                }
                else -> { /* ignored */ }
            }
        }
    }

    override fun loadData() {
        presenter.getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}