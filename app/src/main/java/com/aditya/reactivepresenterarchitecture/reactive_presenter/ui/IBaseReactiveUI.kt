package com.aditya.reactivepresenterarchitecture.reactive_presenter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.IReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider

interface IBaseReactiveUI<P> where  P : IReactivePresenter {
    fun onPresenter(): P
    fun listener() { }
    fun loadData() { }
    fun observeState(lifecycleProvider: IRxLifecycleProvider) { }
}

interface IBaseActivityReactive<P>: IBaseReactiveUI<P> where P : IReactivePresenter {
    fun onCreateView(): View
    fun initViews()
    fun observeState(presenterKey: String, lifecycleProvider: IRxLifecycleProvider) { }
}

interface IBaseFragmentReactive<P>: IBaseReactiveUI<P> where P : IReactivePresenter {
    fun createView(inflater: LayoutInflater, container: ViewGroup?): View
    fun initViews(view: View, savedInstanceState: Bundle?)
    fun observeState(presenterKey: String, lifecycleProvider: IRxLifecycleProvider) { }
}