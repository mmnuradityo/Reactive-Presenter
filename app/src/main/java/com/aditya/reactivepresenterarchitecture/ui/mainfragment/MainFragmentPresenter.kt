package com.aditya.reactivepresenterarchitecture.ui.mainfragment

import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.MainFragmentComponentPresenter
import rx.Observable
import rx.Scheduler

class MainFragmentPresenter(schedulerObserver: Scheduler) : MainFragmentComponentPresenter(
    MainFragmentViewState.Empty, schedulerObserver
) {

    fun getData() {
        val modelView = getViewState().getModelView()
        bindViewState(
            source = Observable.just("OK from Activity Main Fragment")
                .delay(5, java.util.concurrent.TimeUnit.SECONDS),
            success = { newData ->
                MainFragmentViewState.Data(modelView.copy(result = newData))
            },
            loading = MainFragmentViewState.Loading(modelView),
            error = {
                MainFragmentViewState.Error(modelView.copy(error = it.message))
            }
        )
    }

}

