package com.aditya.reactivepresenterarchitecture.ui.nested.fragment

import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child.ChildPresenter
import rx.Observable
import java.util.concurrent.TimeUnit

class ParentPresenter: ChildPresenter(ParentViewState.Empty) {

    fun getData() {
        val modelView = getViewState().getModelView()
        bindViewState(
            source = Observable.just("OK").delay(1, TimeUnit.SECONDS),
            success = {
                ParentViewState.Success(
                    modelView.copy(_result = it)
                )
            },
            loading = ParentViewState.Loading(modelView),
            error = {
                ParentViewState.Error(
                    modelView.copy(_error = it.message)
                )
            }
        )
    }

    fun setCurrentPage(position: Int) {
        getViewState().getModelView().currentPosition = position
    }

}