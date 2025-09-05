package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aditya.reactivepresenterarchitecture.databinding.FragmentNestedDetailBinding
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.reactive_presenter.ui.BaseReactiveFragment
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.DataResult
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.ui.nested.NESTED_FRAGMENT_KEY

class NestedDetailFragment : BaseReactiveFragment<IChildPresenter>(ChildComponentKey.CHILD_DETAIL.value) {

    private var binding: FragmentNestedDetailBinding? = null

    companion object {
        @JvmStatic
        fun newInstance() = NestedDetailFragment().apply {
            arguments = Bundle().apply {
                putString(NESTED_FRAGMENT_KEY, NestedDetailFragment::class.java.simpleName)
            }
        }
    }

    override fun onPresenter(): IChildPresenter = PresenterFactory.obtain()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNestedDetailBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun initViews(view: View, savedInstanceState: Bundle?) { }

    override fun observeState(presenterKey: String, lifecycleProvider: IRxLifecycleProvider) {
        presenter.observeComponentState(presenterKey, lifecycleProvider) { state ->
            when (state) {
                is ChildViewState.Loading -> {
                    if (!validateAndSetup(state.getModelView().result)) {
                        binding?.let {
                            it.progressBar.visibility = View.VISIBLE
                            it.tvError.visibility = View.GONE
                        }
                    }
                }
                is ChildViewState.Data -> {
                    validateAndSetup(state.getModelView().result)
                }
                is ChildViewState.Error -> {
                    if (!validateAndSetup(state.getModelView().result)) {
                        binding?.let {
                            it.progressBar.visibility = View.GONE
                            it.tvError.visibility = View.VISIBLE
                            it.tvError.text = state.getModelView().error ?: "Unknown Error"
                        }
                    }
                }
                else -> { /*ignored*/ }
            }
        }
    }

    private fun validateAndSetup(dataResult: DataResult<out Any?>?): Boolean {
        if (dataResult == null) return false
        val result = dataResult.consume() as? DetailValueItem ?: return false

        binding?.let {
            it.progressBar.visibility = View.GONE
            it.tvError.visibility = View.GONE

            it.tvDetail.text = result.detail
            it.tvTitle.text = result.title
            it.tvMessage.text = result.message
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        presenter.getDetail(presenterKey)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}