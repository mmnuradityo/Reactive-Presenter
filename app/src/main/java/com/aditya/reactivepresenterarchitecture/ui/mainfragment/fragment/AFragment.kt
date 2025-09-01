package com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aditya.reactivepresenterarchitecture.R
import com.aditya.reactivepresenterarchitecture.databinding.FragmentLayoutBinding
import com.aditya.reactivepresenterarchitecture.manager.PresenterInjector
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.FRAGMENT_KEY
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentComponentViewState
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentPresenterKey

class AFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = AFragment().apply {
            arguments = Bundle().apply {
                putString(FRAGMENT_KEY, AFragment::class.java.simpleName)
            }
        }
    }
    private lateinit var binding: FragmentLayoutBinding
    private lateinit var presenter: IMainFragmentComponentPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = (activity as PresenterInjector<*>).obtainPresenter(
            IMainFragmentComponentPresenter::class.java
        ) as IMainFragmentComponentPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.observeComponentViewState {
            when (it) {
                is MainFragmentComponentViewState.Empty -> {
                    binding.tvText.text = "Greetings from A"
                }

                is MainFragmentComponentViewState.Loading -> {
                    binding.tvText.text = it.getModelView().result.ifEmpty { "Loading A...." }
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
                ContextCompat.getColor(it, R.color.pink)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(MainFragmentPresenterKey.A.value)
        presenter.getDataComponent()
    }

    override fun onPause() {
        presenter.detachView()
        super.onPause()
    }
}