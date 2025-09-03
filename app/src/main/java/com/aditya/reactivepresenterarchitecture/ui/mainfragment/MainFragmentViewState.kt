package com.aditya.reactivepresenterarchitecture.ui.mainfragment;

import com.aditya.reactivepresenterarchitecture.reactive_presenter.ComponentViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.ModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.ViewState

data class MainFragmentModelView(
    val result: String = "",
    val error: String? = null,
) : ModelView()

sealed class MainFragmentViewState(
    modelView: MainFragmentModelView,
) : ViewState<MainFragmentModelView>(modelView) {

    data object Empty : MainFragmentViewState(
        MainFragmentModelView()
    )

    data class Loading(
        val model: MainFragmentModelView,
    ) : MainFragmentViewState(model)

    data class Data(
        val model: MainFragmentModelView,
    ) : MainFragmentViewState(model)

    data class Error(
        val model: MainFragmentModelView,
    ) : MainFragmentViewState(model)

}

data class MainFragmentComponentModelView(
    val result: String = "",
    val error: String? = null,
) : ModelView()

sealed class MainFragmentComponentViewState(
    modelView: MainFragmentComponentModelView,
) : ComponentViewState<MainFragmentComponentModelView>(modelView) {

    data object Empty : MainFragmentComponentViewState(MainFragmentComponentModelView())

    data class Loading(
        val model: MainFragmentComponentModelView,
    ) : MainFragmentComponentViewState( model)

    data class Data(
        val model: MainFragmentComponentModelView,
    ) : MainFragmentComponentViewState(model)

    data class Error(
        val model: MainFragmentComponentModelView,
    ) : MainFragmentComponentViewState(model)

}

enum class ComponentPresenterKey(val value: String) {
    A("Component_A"),
    B("Component_B");
}