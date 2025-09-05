package com.aditya.reactivepresenterarchitecture.ui.nested.fragment.child

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.CompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.DataResult
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ICompositeReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.ParentViewState
import rx.Observable
import java.util.concurrent.TimeUnit

abstract class ChildPresenter(
    viewState: ParentViewState
): CompositeReactivePresenter<ParentViewState, ChildViewState<*>>(
    viewState,
    mutableMapOf(
        ChildComponentKey.CHILD_LIST.value to ChildViewState.Empty<List<String>>(),
        ChildComponentKey.CHILD_DETAIL.value to ChildViewState.Empty<String>()
    )
), IChildPresenter {

    override fun getList(key: String) {
        val modelView = getComponentModelView<List<ListValueItem>>()
        bindComponentState(
            key = key,
            source = Observable.just(
                listOf(
                    ListValueItem(1L, "A"),
                    ListValueItem(2L, "B"),
                    ListValueItem(3L, "C"),
                    ListValueItem(4L, "D"),
                    ListValueItem(5L, "E"),
                    ListValueItem(6L, "F"),
                    ListValueItem(7L, "G"),
                    ListValueItem(8L, "H"),
                    ListValueItem(9L, "I"),
                    ListValueItem(10L, "J")
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
        val modelView = getComponentModelView<DetailValueItem>()
        bindComponentState(
            key = key,
            source = Observable.just(
                DetailValueItem(
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> getComponentModelView(): ChildModelView<T> {
        return (getComponentState() as? ChildViewState<T> ?: ChildViewState.Empty()).getModelView()
    }

}

interface IChildPresenter: ICompositeReactivePresenter<ChildViewState<*>> {
    fun getList(key: String)
    fun getDetail(key: String)
}