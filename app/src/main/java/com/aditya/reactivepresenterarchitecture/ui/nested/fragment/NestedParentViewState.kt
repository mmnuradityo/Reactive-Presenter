package com.aditya.reactivepresenterarchitecture.ui.nested.fragment

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState

data class ParentModelView(
    var currentPosition: Int = 0,
    val result: String? = null,
    val error: String? = null
): ModelView()

sealed class ParentViewState(
    id: Int, modelView: ParentModelView
): ViewState<ParentModelView>(id, modelView) {
    data object Empty : ParentViewState(0, ParentModelView())
    data class Loading(val model: ParentModelView) : ParentViewState(1, model)
    data class Success(val model: ParentModelView) : ParentViewState(2, model)
    data class Error(val model: ParentModelView) : ParentViewState(3, model)
}