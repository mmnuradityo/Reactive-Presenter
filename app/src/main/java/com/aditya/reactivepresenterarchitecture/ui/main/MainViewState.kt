package com.aditya.reactivepresenterarchitecture.ui.main

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState

data class MainModelView(
    val list: List<String> = emptyList(),
    val error: String? = null
) : ModelView()

sealed class MainViewState(
    modelView: MainModelView
) : ViewState<MainModelView>(modelView) {

    data object Empty : MainViewState(MainModelView())

    data class Loading(val model: MainModelView) : MainViewState(model)

    data class StringList(val model: MainModelView) : MainViewState(model)

    data class Error(val model: MainModelView) : MainViewState(model)

}