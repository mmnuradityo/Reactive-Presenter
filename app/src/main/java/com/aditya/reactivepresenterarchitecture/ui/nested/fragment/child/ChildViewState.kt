package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ComponentViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.DataResult
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView

data class ListValueItem(
    val id: Long,
    val text: String
)

data class DetailValueItem(
    val id: Long,
    val title: String,
    val detail: String,
    val message: String
)

data class ChildModelView<T>(
    val result: DataResult<T>? = null,
    val error: String? = null,
): ModelView()

sealed class ChildViewState<T>(
    modelView: ChildModelView<T>
): ComponentViewState<ChildModelView<T>>(modelView) {
    data class Empty<T>(val model: ChildModelView<T> = ChildModelView()) : ChildViewState<T>(model)
    data class Loading<T>(val model: ChildModelView<T>) : ChildViewState<T>(model)
    data class Data<T>(val model: ChildModelView<T>) : ChildViewState<T>(model)
    data class Error<T>(val model: ChildModelView<T>) : ChildViewState<T>(model)
}

enum class ChildComponentKey(val value: String) {
    CHILD_LIST("Child_List"),
    CHILD_DETAIL("Child_Detail"),
}