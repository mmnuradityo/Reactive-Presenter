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

    override fun getList(key: String, page: Int) {
        val modelView = getComponentModelView<ListModel>(key)
        bindComponentState(
            key = key,
            source = Observable.fromCallable {
                val list = mutableListOf<ListValueItem>()
                for (i in page until (page + 20)) {
                    list.add(
                        ListValueItem(i.toLong(), "Item $i")
                    )
                }
                ListModel(
                    currentPage = page, list = list
                )
            }.delay(5, TimeUnit.SECONDS),
            loading = ChildViewState.Loading(modelView),
            success = {
                ChildViewState.Data(
                    modelView.copy(_result = DataResult(data = it))
                )
            },
            error = {
                ChildViewState.Error(
                    modelView.copy(_error = it.message)
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
                    modelView.copy(_result = DataResult(data = it))
                )
            },
            error = {
                ChildViewState.Error(
                    modelView.copy(_error = it.message)
                )
            }
        )
    }

    override fun saveState(key: String, savedState: Bundle?) {
        val modelView: ChildModelView<*> = when (key) {
            ChildComponentKey.CHILD_LIST.value -> getComponentModelView<ListModel>(key)
            ChildComponentKey.CHILD_DETAIL.value -> getComponentModelView<DetailModel>(key)
            else -> throw IllegalArgumentException("Unknown component key: $key")
        }

        emitComponentState(
            key = key,
            newViewState = ChildViewState.StateChange(
                modelView.copy(_uiState = savedState)
            ).apply {
                setComponentKey(key)
            }
        )
    }

    override fun restoreState(key: String) {
        val modelView: ChildModelView<*> = when (key) {
            ChildComponentKey.CHILD_LIST.value -> getComponentModelView<ListModel>(key)
            ChildComponentKey.CHILD_DETAIL.value -> getComponentModelView<DetailModel>(key)
            else -> throw IllegalArgumentException("Unknown component key: $key")
        }

        emitComponentState(
            key = key,
            newViewState = ChildViewState.StateChange(
                modelView.copy(_uiState = null).apply { setConsume(true) }
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
    fun getList(key: String, page: Int)
    fun getDetail(key: String)
    fun saveState(key: String, savedState: Bundle?)
    fun restoreState(key: String)
}