package com.aditya.reactivepresenterarchitecture.reactive_presenter.base

abstract class ViewState<MV: ModelView>(
    private val modelView: MV
) {
    fun getModelView(): MV = modelView
}

abstract class ComponentViewState<MV : ModelView>(
    modelView: MV
): ViewState<MV>(modelView) {
    private var _key: String = ""

    fun setComponentKey(key: String) {
        this._key = key
    }

    fun getComponentKey(): String = _key
}

abstract class ModelView {
    private var isConsume: Boolean = false

    fun setConsume(isConsumer: Boolean) {
        this.isConsume = isConsumer
    }

    fun isConsume(): Boolean = isConsume
}