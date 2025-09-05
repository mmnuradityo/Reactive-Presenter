package com.aditya.reactivepresenterarchitecture.ui.nested.fragment

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState

data class ParentModelView(
    var currentPosition: Int = 0,
    val result: String? = null,
    val error: String? = null
): ModelView()

sealed class ParentViewState(modelView: ParentModelView): ViewState<ParentModelView>(modelView) {
    data object Empty : ParentViewState(ParentModelView())
    data class Loading(val model: ParentModelView) : ParentViewState(model)
    data class Success(val model: ParentModelView) : ParentViewState(model)
    data class Error(val model: ParentModelView) : ParentViewState(model)
}