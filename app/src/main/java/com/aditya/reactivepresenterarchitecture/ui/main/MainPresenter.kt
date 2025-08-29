package com.aditya.reactivepresenterarchitecture.ui.main

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.manager.ReactivePresenter
import com.aditya.reactivepresenterarchitecture.manager.ViewState
import rx.Observable
import rx.Scheduler
import java.util.concurrent.TimeUnit

class MainPresenter(
    lifecycle: Lifecycle, schedulerObserver: Scheduler
): ReactivePresenter<MainViewState>(
    MainViewState.Empty, lifecycle, schedulerObserver
) {

    private val list = mutableListOf<String>()

    init {
        list.add("OK")
        list.add("OK GA NIH")
        list.add("OK lho lah")
        list.add("loh kok OK")
    }

    fun getList() {
        val modelView = getViewState().modelView
        transformAndSubscribe(
            source = Observable.just(list).delay(5, TimeUnit.SECONDS),
            loading = MainViewState.Loading(modelView),
            success = { newList ->
                MainViewState.StringList(modelView.copy(list = newList))
            },
            error = {
                MainViewState.Error(modelView.copy(error = it.message))
            }
        )
    }
}

data class MainModelView(
    val list: List<String> = emptyList(),
    val error: String? = null
)

sealed class MainViewState(val modelView: MainModelView): ViewState {
    data object Empty: MainViewState(MainModelView())
    data class Loading(val model: MainModelView): MainViewState(model)
    data class StringList(val model: MainModelView): MainViewState(model)
    data class Error(val model: MainModelView): MainViewState(model)
}