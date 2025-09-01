package com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.manager.INestedReactivePresenter
import com.aditya.reactivepresenterarchitecture.manager.NestedReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentComponentModelView
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentComponentViewState
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentPresenterKey
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentViewState
import rx.Observable
import rx.Scheduler
import java.util.concurrent.TimeUnit

abstract class MainFragmentComponentPresenter(
    viewState: MainFragmentViewState, lifecycle: Lifecycle, schedulerObserver: Scheduler
): NestedReactivePresenter<MainFragmentViewState, MainFragmentComponentViewState>(
    viewState,
    mutableMapOf(
        MainFragmentPresenterKey.A.value to MainFragmentComponentViewState.Empty,
        MainFragmentPresenterKey.B.value to MainFragmentComponentViewState.Empty
    ),
    lifecycle, schedulerObserver
), IMainFragmentComponentPresenter {

    override fun getDataComponent() {
        val modelView = getComponentModelView()
        bindComponentViewState(
            source = Observable.just("OK from $componentKey")
                .delay(5, TimeUnit.SECONDS),
            loading = MainFragmentComponentViewState.Loading(modelView),
            success = { newData ->
                MainFragmentComponentViewState.Data(model = modelView.copy(result = newData))
            },
            error = {
                MainFragmentComponentViewState.Error(modelView.copy(error = it.message))
            }
        )
    }

    private fun getComponentModelView(): MainFragmentComponentModelView {
       return getComponentViewState()?.getModelView() ?: MainFragmentComponentViewState.Empty.getModelView()
    }
}

interface IMainFragmentComponentPresenter: INestedReactivePresenter<MainFragmentComponentViewState> {
    fun getDataComponent()
}