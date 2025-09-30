package com.aditya.reactivepresenterarchitecture.reactive_presenter.base

abstract class ViewState<MV: ModelView>(
    private val _id: Int = 1,
    private val modelView: MV
) {
    fun getId(): Int = _id

    fun getModelView(): MV = modelView

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ViewState<MV>
        return this._id == other.getId()
    }

    override fun hashCode(): Int {
        var result = _id
        result = 31 * result + modelView.hashCode()
        return result
    }
}

abstract class ComponentViewState<MV : ModelView>(
    componentId: Int = 1, modelView: MV
): ViewState<MV>(componentId, modelView) {
    private var _key: String = ""

    fun setComponentKey(key: String) {
        this._key = key
    }

    fun getComponentKey(): String = _key
}

abstract class ModelView {
    private var isConsume: Boolean = false

    fun setConsume(isConsume: Boolean) {
        this.isConsume = isConsume
    }

    fun isConsume(): Boolean = isConsume

    protected fun <T> consume(value: T): T {
        setConsume(true)
        return value
    }
}

data class DataResult<T>(var data: T?)
