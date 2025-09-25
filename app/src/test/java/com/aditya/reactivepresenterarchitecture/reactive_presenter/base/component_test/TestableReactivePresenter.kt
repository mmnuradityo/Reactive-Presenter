package com.aditya.reactivepresenterarchitecture.reactive_presenter.base.component_test

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import rx.Observable
import rx.functions.Func1

class TestableReactivePresenter(
    initialState: TestViewState,
    schedulerProvider: ISchedulerProvider,
) : ReactivePresenter<TestViewState>(initialState, schedulerProvider) {

    fun testBindViewState(
        source: Observable<String>,
        success: Func1<String, TestViewState>?,
        loading: TestViewState?,
        error: Func1<Throwable, TestViewState>?,
    ) {
        bindViewState(source, success, loading, error)
    }

    fun testableValidateSameValue(oldState: ViewState<*>?, newState: ViewState<*>?): Boolean {
        return validateSameValue(oldState, newState)
    }

    fun testableDestroy() {
        destroy()
    }

}

