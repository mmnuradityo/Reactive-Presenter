package com.aditya.reactivepresenterarchitecture.ui.mainfragment;

import com.aditya.reactivepresenterarchitecture.manager.ModelView
import com.aditya.reactivepresenterarchitecture.manager.ViewState

data class MainFragmentModelView(
    val result: String = "",
    val error: String? = null
) : ModelView {
    private var isDone: Boolean = false

    override fun setDone(isDone: Boolean) {
        this.isDone = isDone
    }

    override fun isDone(): Boolean = isDone
}

sealed class MainFragmentViewState(
   private val modelView: MainFragmentModelView
): ViewState<MainFragmentModelView> {
    data object Empty: MainFragmentViewState(MainFragmentModelView())
    data class Loading(val model: MainFragmentModelView): MainFragmentViewState(model)
    data class Data(val model: MainFragmentModelView): MainFragmentViewState(model)
    data class Error(val model: MainFragmentModelView): MainFragmentViewState(model)

    override fun getModelView(): MainFragmentModelView = modelView
}

data class MainFragmentComponentModelView(
    val result: String = "",
    val error: String? = null
) : ModelView {
    private var isDone: Boolean = false

    override fun setDone(isDone: Boolean) {
        this.isDone = isDone
    }

    override fun isDone(): Boolean = isDone
}

sealed class MainFragmentComponentViewState(
    private val modelView: MainFragmentComponentModelView
): ViewState<MainFragmentComponentModelView> {
    data object Empty: MainFragmentComponentViewState(MainFragmentComponentModelView())
    data class Loading(val model: MainFragmentComponentModelView): MainFragmentComponentViewState(model)
    data class Data(val model: MainFragmentComponentModelView): MainFragmentComponentViewState(model)
    data class Error(val model: MainFragmentComponentModelView): MainFragmentComponentViewState(model)

    override fun getModelView(): MainFragmentComponentModelView = modelView
}

enum class MainFragmentPresenterKey(val value: String) {
    A("ComponentA"),
    B("ComponentB");
}