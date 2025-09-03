package com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.CompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ICompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.ComponentPresenterKey
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentComponentModelView
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentComponentViewState
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentViewState
import rx.Observable
import java.util.concurrent.TimeUnit

abstract class MainFragmentComponentPresenter(viewState: MainFragmentViewState) :
    CompositeReactivePresenter<MainFragmentViewState, MainFragmentComponentViewState>(
        viewState,
        mutableMapOf(
            ComponentPresenterKey.A.value to MainFragmentComponentViewState.Empty,
            ComponentPresenterKey.B.value to MainFragmentComponentViewState.Empty
        )
    ), IMainFragmentComponentPresenter {

    override fun getDataComponent() {
        val modelView = getComponentModelView()
        bindComponentState(
            source = Observable.just("OK from ${componentKey.get()}")
                .delay(5, TimeUnit.SECONDS),
            success = { newData ->
                MainFragmentComponentViewState.Data(
                    model = modelView.copy(result = newData)
                )
            },
            loading = MainFragmentComponentViewState.Loading(
                model = modelView
            ),
            error = {
                MainFragmentComponentViewState.Error(
                    model = modelView.copy(error = it.message)
                )
            }
        )
    }

    private fun getComponentModelView(): MainFragmentComponentModelView {
        return (getComponentState() ?: MainFragmentComponentViewState.Empty).getModelView()
    }
}

interface IMainFragmentComponentPresenter :
    ICompositeReactivePresenter<MainFragmentComponentViewState> {
    fun getDataComponent()
}