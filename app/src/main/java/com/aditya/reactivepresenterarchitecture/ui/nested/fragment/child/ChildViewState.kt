package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child

import android.os.Bundle
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ComponentViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.DataResult
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ModelView

data class ListValueItem(
    val id: Long,
    val text: String
)

data class ListModel(
    val currentPage: Int = 0,
    val list: MutableList<ListValueItem> = mutableListOf()
)

data class DetailModel(
    val id: Long,
    val title: String,
    val detail: String,
    val message: String
)

data class ChildModelView<T>(
    var uiState: Bundle? = null,
    val result: DataResult<T>? = null,
    val error: String? = null,
): ModelView()

sealed class ChildViewState<T>(
    id: Int, modelView: ChildModelView<T>
): ComponentViewState<ChildModelView<T>>(id, modelView) {
    data class Empty<T>(val model: ChildModelView<T> = ChildModelView()) : ChildViewState<T>(0,model)
    data class StateChange<T>(val model: ChildModelView<T>) : ChildViewState<T>(1,model)
    data class Loading<T>(val model: ChildModelView<T>) : ChildViewState<T>(2,model)
    data class Data<T>(val model: ChildModelView<T>) : ChildViewState<T>(3,model)
    data class Error<T>(val model: ChildModelView<T>) : ChildViewState<T>(4,model)
}

enum class ChildComponentKey(val value: String) {
    CHILD_LIST("Child_List"),
    CHILD_DETAIL("Child_Detail"),
}