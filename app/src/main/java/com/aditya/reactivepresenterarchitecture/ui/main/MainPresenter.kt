package com.aditya.reactivepresenterarchitecture.ui.main

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ReactivePresenter
import rx.Observable
import java.util.concurrent.TimeUnit

class MainPresenter: ReactivePresenter<MainViewState>(MainViewState.Empty) {

    private val list = mutableListOf<String>()

    init {
        list.add("OK")
        list.add("OK GA NIH")
        list.add("OK lho lah")
        list.add("loh kok OK")
    }

    fun getList() {
        val modelView = getViewState().getModelView()
        bindViewState(
            source = Observable.just(list).delay(5, TimeUnit.SECONDS),
            success = { newList ->
                MainViewState.StringList(modelView.copy(list = newList))
            },
            loading = MainViewState.Loading(modelView),
            error = {
                MainViewState.Error(modelView.copy(error = it.message))
            }
        )
    }
}