package com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aditya.reactivepresenterarchitecture.R
import com.aditya.reactivepresenterarchitecture.databinding.FragmentLayoutBinding
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.ComponentPresenterKey
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.FRAGMENT_KEY
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentComponentViewState

class BFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = BFragment().apply {
            arguments = Bundle().apply {
                putString(FRAGMENT_KEY, BFragment::class.java.simpleName)
            }
        }
    }
    private val presenter: IMainFragmentComponentPresenter = PresenterFactory.obtain()
    private lateinit var binding: FragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.observeComponentState(ComponentPresenterKey.B.value, lifecycle) {
            when (it) {
                is MainFragmentComponentViewState.Empty -> {
                    binding.tvText.text = "Greetings from B"
                }

                is MainFragmentComponentViewState.Loading -> {
                    binding.tvText.text = it.getModelView().result.ifEmpty { "Loading B...." }
                }

                is MainFragmentComponentViewState.Data -> {
                    binding.tvText.text = it.getModelView().result
                }

                is MainFragmentComponentViewState.Error -> {
                    binding.tvText.text = it.getModelView().error
                }
            }
        }
        context?.let {
            binding.root.setBackgroundColor(
                ContextCompat.getColor(it, R.color.yellow)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getDataComponent()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            presenter.attachView(ComponentPresenterKey.B.value)
            presenter.getDataComponent()
        } else {
            presenter.detachView()

        }
    }

}