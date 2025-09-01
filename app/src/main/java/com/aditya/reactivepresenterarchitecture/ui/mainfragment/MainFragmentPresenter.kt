package com.aditya.reactivepresenterarchitecture.ui.mainfragment

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.MainFragmentComponentPresenter
import rx.Observable
import rx.Scheduler

class MainFragmentPresenter(
    lifecycle: Lifecycle, schedulerObserver: Scheduler,
) : MainFragmentComponentPresenter(
    MainFragmentViewState.Empty, lifecycle, schedulerObserver
) {

    fun getData() {
        val modelView = getViewState().getModelView()
        bindViewState(
            source = Observable.just("OK from Activity Main Fragment")
                .delay(5, java.util.concurrent.TimeUnit.SECONDS),
            loading = MainFragmentViewState.Loading(modelView),
            success = { newData ->
                MainFragmentViewState.Data(modelView.copy(result = newData))
            },
            error = {
                MainFragmentViewState.Error(modelView.copy(error = it.message))
            }
        )
    }

}

