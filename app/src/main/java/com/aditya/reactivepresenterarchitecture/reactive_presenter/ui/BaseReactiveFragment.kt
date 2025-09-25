package com.aditya.reactivepresenterarchitecture.reactive_presenter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.IReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.RxLifecycleProvider

abstract class BaseReactiveFragment<P>(
    protected val presenterKey: String = ""
): Fragment(), IBaseFragmentReactive<P> where  P : IReactivePresenter {

    protected val ctx: Context
        get() = requireContext()
    protected val presenter: P = this.onPresenter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = createView(inflater, container)
        initViews(view, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lifecycleProvider: IRxLifecycleProvider = RxLifecycleProvider(lifecycle)
        if (presenterKey.isEmpty()) observeState(lifecycleProvider)
        else observeState(presenterKey, lifecycleProvider)
        listener()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        this.presenter.attachView(presenterKey)
    }

    override fun onPause() {
        super.onPause()
        this.presenter.detachView(presenterKey)
    }

}