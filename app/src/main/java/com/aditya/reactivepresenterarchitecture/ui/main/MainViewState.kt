package com.aditya.reactivepresenterarchitecture.ui.main

import com.aditya.reactivepresenterarchitecture.manager.ModelView
import com.aditya.reactivepresenterarchitecture.manager.ViewState

data class MainModelView(
    val list: List<String> = emptyList(),
    val error: String? = null
) : ModelView {
    private var isDone: Boolean = false

    override fun setDone(isDone: Boolean) {
        this.isDone = isDone
    }

    override fun isDone(): Boolean = isDone
}

sealed class MainViewState(
    private val modelView: MainModelView
): ViewState<MainModelView> {
    data object Empty: MainViewState(MainModelView())
    data class Loading(val model: MainModelView): MainViewState(model)
    data class StringList(val model: MainModelView): MainViewState(model)
    data class Error(val model: MainModelView): MainViewState(model)

    override fun getModelView(): MainModelView = modelView
}