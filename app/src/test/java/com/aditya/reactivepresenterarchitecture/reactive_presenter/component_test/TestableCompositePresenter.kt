package com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ComponentViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.CompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import rx.Observable
import rx.functions.Func1

const val TEST_COMPONENT_KEY = "test_component_key"
const val TEST_NOT_FOUND_COMPONENT_KEY = "test_not_found_component_key"

class TestableCompositePresenter(
    parentViewState: TestViewState,
    schedulerProvider: ISchedulerProvider
) : CompositeReactivePresenter<TestViewState, TestChildViewState>(
    parentViewState,
    mutableMapOf(TEST_COMPONENT_KEY to TestChildViewState()),
    schedulerProvider
) {

    fun testBindViewState(
        key: String,
        source: Observable<String>,
        success: Func1<String, TestChildViewState>,
        loading: TestChildViewState?,
        error: Func1<Throwable, TestChildViewState>?,
    ) {
        bindComponentState(key, source, success, loading, error)
    }

    fun testBindViewStateWithoutLoadingAndError(
        key: String,
        source: Observable<TestModelView>,
        success: Func1<TestModelView, TestChildViewState>,
    ) {
        bindComponentState(key, source, success)
    }

    fun testableValidateConsumed(newState: ViewState<*>?): Boolean {
        return validateConsumed(newState)
    }

    fun testableDestroy() {
        destroy()
    }

}

class TestChildViewState(
    id: Int = 0,
    model: TestModelView = TestModelView(),
    val specificData: String = "state-${model.stringId}",
) : ComponentViewState<TestModelView>(id, model) {

    override fun toString(): String {
        return "TestChildViewState(" +
                "key=${getComponentKey()}, " +
                "modelId=${getModelView().stringId}, " +
                "specificData='$specificData', " +
                "consumed=${getModelView().isConsume()})"
    }

}