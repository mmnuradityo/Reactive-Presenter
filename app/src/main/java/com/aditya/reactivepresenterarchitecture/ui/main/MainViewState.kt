package com.aditya.reactivepresenterarchitecture.ui.main

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState

data class MainModelView(
    val list: List<String> = emptyList(),
    val error: String? = null
) : ModelView()

sealed class MainViewState(
    id: Int, modelView: MainModelView
) : ViewState<MainModelView>(id, modelView) {

    data object Empty : MainViewState(0, MainModelView())

    data class Loading(val model: MainModelView) : MainViewState(1, model)

    data class StringList(val model: MainModelView) : MainViewState(2, model)

    data class Error(val model: MainModelView) : MainViewState(3, model)

}