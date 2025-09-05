package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aditya.reactivepresenterarchitecture.databinding.FragmentNestedListBinding
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.reactive_presenter.ui.BaseReactiveFragment
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.DataResult
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.ui.nested.NESTED_FRAGMENT_KEY
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.ChildComponentKey
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.ChildViewState
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.IChildPresenter
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.ListValueItem

class NestedListFragment : BaseReactiveFragment<IChildPresenter>(ChildComponentKey.CHILD_LIST.value) {

    private var binding: FragmentNestedListBinding? = null
    private val rvAdapter = RvAdapter()

    companion object {
        @JvmStatic
        fun newInstance() = NestedListFragment().apply {
            arguments = Bundle().apply {
                putString(NESTED_FRAGMENT_KEY, NestedListFragment::class.java.simpleName)
            }
        }
    }

    override fun onPresenter(): IChildPresenter = PresenterFactory.obtain()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNestedListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = rvAdapter
        }
    }

    override fun observeState(presenterKey: String, lifecycleProvider: IRxLifecycleProvider) {
        presenter.observeComponentState(presenterKey, lifecycleProvider) { state ->
            when (state) {
                is ChildViewState.Loading -> {
                    if (!validateAndSetupList(state.getModelView().result)) {
                        binding?.let {
                            it.progressBar.visibility = View.VISIBLE
                            it.tvError.visibility = View.GONE
                            it.recyclerView.visibility = View.GONE
                        }
                    }
                }

                is ChildViewState.Data -> {
                    validateAndSetupList(state.getModelView().result)
                }

                is ChildViewState.Error -> {
                    if (!validateAndSetupList(state.getModelView().result)) {
                        binding?.let {
                            it.progressBar.visibility = View.GONE
                            it.tvError.visibility = View.VISIBLE
                            it.recyclerView.visibility = View.GONE

                            it.tvError.text = state.getModelView().error
                        }
                    }
                }

                else -> { /* ignored */ }
            }
        }
    }

    private fun validateAndSetupList(result: DataResult<out Any?>?): Boolean {
        if (result == null) return false
        @Suppress("UNCHECKED_CAST")
        setupList(result as DataResult<List<ListValueItem>>)
        return true
    }

    private fun setupList(result: DataResult<List<ListValueItem>>) {
        val list = result.consume() ?: return
        binding?.let {
            it.progressBar.visibility = View.GONE
            it.tvError.visibility = View.GONE
            it.recyclerView.visibility = View.VISIBLE
        }
        rvAdapter.submitList(list)
    }

    override fun onResume() {
        super.onResume()
        presenter.getList(presenterKey)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}