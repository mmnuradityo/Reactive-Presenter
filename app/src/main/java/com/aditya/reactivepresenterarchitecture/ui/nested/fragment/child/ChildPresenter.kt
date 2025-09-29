package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child

import android.os.Bundle
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.CompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.DataResult
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ICompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.ParentViewState
import rx.Observable
import java.util.concurrent.TimeUnit

abstract class ChildPresenter(
    viewState: ParentViewState,
): CompositeReactivePresenter<ParentViewState, ChildViewState<*>>(
    viewState,
    mutableMapOf(
        ChildComponentKey.CHILD_LIST.value to ChildViewState.Empty<ListModel>(),
        ChildComponentKey.CHILD_DETAIL.value to ChildViewState.Empty<String>()
    )
), IChildPresenter {

    companion object {
        const val RV_LIST_STATE: String = "list_state"
    }

    override fun getList(key: String) {
        val modelView = getComponentModelView<ListModel>(key)
        bindComponentState(
            key = key,
            source = Observable.just(
                ListModel(
                    list = listOf(
                        ListValueItem(1L, "A"),
                        ListValueItem(2L, "B"),
                        ListValueItem(3L, "C"),
                        ListValueItem(4L, "D"),
                        ListValueItem(5L, "E"),
                        ListValueItem(6L, "F"),
                        ListValueItem(7L, "G"),
                        ListValueItem(8L, "H"),
                        ListValueItem(9L, "I"),
                        ListValueItem(10L, "J"),
                        ListValueItem(11L, "K"),
                        ListValueItem(12L, "L"),
                        ListValueItem(13L, "M"),
                        ListValueItem(14L, "N"),
                        ListValueItem(15L, "O"),
                        ListValueItem(16L, "P"),
                        ListValueItem(17L, "Q"),
                        ListValueItem(18L, "R"),
                        ListValueItem(19L, "S"),
                        ListValueItem(20L, "T"),
                        ListValueItem(21L, "U"),
                        ListValueItem(22L, "V"),
                        ListValueItem(23L, "W"),
                        ListValueItem(24L, "x"),
                        ListValueItem(25L, "Y"),
                        ListValueItem(26L, "Z")
                    )
                )
            ).delay(5, TimeUnit.SECONDS),
            loading = ChildViewState.Loading(modelView),
            success = {
                ChildViewState.Data(
                    modelView.copy(result = DataResult(data = it))
                )
            },
            error = {
                ChildViewState.Error(
                    modelView.copy(error = it.message)
                )
            }
        )
    }

    override fun getDetail(key: String) {
        val modelView = getComponentModelView<DetailModel>(key)
        bindComponentState(
            key = key,
            source = Observable.just(
                DetailModel(
                    1L,
                    "Nested Title",
                    "Nested Detail",
                    "This is detail message to Nested Detail Fragment"
                )
            ).delay(5, TimeUnit.SECONDS),
            loading = ChildViewState.Loading(modelView),
            success = {
                ChildViewState.Data(
                    modelView.copy(result = DataResult(data = it))
                )
            },
            error = {
                ChildViewState.Error(
                    modelView.copy(error = it.message)
                )
            }
        )
    }

    override fun saveState(key: String, savedState: Bundle?) {
        if (savedState == null) return
        val modelView = getComponentModelView<ListModel>(key)

        emitComponentState(
            key = key,
            newViewState = ChildViewState.StateChange(
                modelView.copy(
                    result = DataResult(
                        data = modelView.result?.consume()?.copy(state = savedState)
                    )
                )
            ).apply {
                setComponentKey(key)
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getComponentModelView(key: String): ChildModelView<T> {
        return (getComponentState(key) as? ChildViewState<T> ?: ChildViewState.Empty()).getModelView()
    }

}

interface IChildPresenter: ICompositeReactivePresenter<ChildViewState<*>> {
    fun getList(key: String)
    fun getDetail(key: String)
    fun saveState(key: String, savedState: Bundle?)
}