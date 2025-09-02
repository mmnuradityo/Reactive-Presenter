package com.aditya.reactivepresenterarchitecture.reactive_presenter

abstract class ViewState<MV: ModelView>(
    private val modelView: MV
) {
    fun getModelView(): MV = modelView
}

abstract class ModelView {
    private var isDone: Boolean = false

    fun setDone(isDone: Boolean) {
        this.isDone = isDone
    }

    fun isDone(): Boolean = isDone
}