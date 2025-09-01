package com.aditya.reactivepresenterarchitecture.manager

fun interface ViewState<MV: ModelView> {
    fun getModelView(): MV
}

interface ModelView {
    fun setDone(isDone: Boolean)
    fun isDone(): Boolean
}