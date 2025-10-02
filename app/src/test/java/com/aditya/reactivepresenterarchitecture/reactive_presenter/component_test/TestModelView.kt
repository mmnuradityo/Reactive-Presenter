package com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState

open class TestModelView(val stringId: String = "defaultId") : ModelView() {

    fun get(): String = stringId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TestModelView
        if (stringId != other.stringId) return false
        return true
    }

    override fun hashCode(): Int {
        return stringId.hashCode()
    }
}

class TestViewState(
    id: Int = 0,
    model: TestModelView = TestModelView(),
    val specificData: String = "state-${model.stringId}",
) : ViewState<TestModelView>(id, model) {

    override fun toString(): String {
        return "TestViewState(modelId=${getModelView().stringId}, specificData='$specificData', consumed=${getModelView().isConsume()})"
    }

}