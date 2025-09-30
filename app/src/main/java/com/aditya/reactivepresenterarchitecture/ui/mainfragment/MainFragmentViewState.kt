package com.aditya.reactivepresenterarchitecture.ui.mainfragment;

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ComponentViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ViewState

data class MainFragmentModelView(
    private val _result: String = "",
    private val _error: String? = null,
) : ModelView() {
    val result get() = consume(_result)
    val error get() = consume(_error)
}

sealed class MainFragmentViewState(
    id: Int, modelView: MainFragmentModelView
) : ViewState<MainFragmentModelView>(id, modelView) {

    data object Empty : MainFragmentViewState(
        0,MainFragmentModelView()
    )

    data class Loading(
        val model: MainFragmentModelView,
    ) : MainFragmentViewState(1, model)

    data class Data(
        val model: MainFragmentModelView,
    ) : MainFragmentViewState(2, model)

    data class Error(
        val model: MainFragmentModelView,
    ) : MainFragmentViewState(3, model)

}

data class MainFragmentComponentModelView(
    private val _result: String = "",
    private val _error: String? = null,
) : ModelView() {
    val result get() = consume(_result)
    val error get() = consume(_error)
}

sealed class MainFragmentComponentViewState(
    id: Int, modelView: MainFragmentComponentModelView
) : ComponentViewState<MainFragmentComponentModelView>(id, modelView) {

    data object Empty : MainFragmentComponentViewState(
        0, MainFragmentComponentModelView()
    )

    data class Loading(
        val model: MainFragmentComponentModelView,
    ) : MainFragmentComponentViewState( 1, model)

    data class Data(
        val model: MainFragmentComponentModelView,
    ) : MainFragmentComponentViewState(2, model)

    data class Error(
        val model: MainFragmentComponentModelView,
    ) : MainFragmentComponentViewState(3, model)

}

enum class ComponentPresenterKey(val value: String) {
    A("Component_A"),
    B("Component_B");
}