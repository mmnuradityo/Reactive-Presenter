package com.aditya.reactivepresenterarchitecture.reactive_presenter

import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.ReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.main.MainPresenter
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentPresenter

object PresenterFactory {

    val presentersCache = mutableMapOf<String, ReactivePresenter<*>>()

    @JvmStatic
    inline fun <reified P> getOrCreate(): P where P : ReactivePresenter<*> {
        val key = P::class.java.simpleName
        return presentersCache.getOrPut(P::class.java.simpleName) {
            when (key) {
                MainPresenter::class.java.simpleName -> MainPresenter()
                MainFragmentPresenter::class.java.simpleName -> MainFragmentPresenter()
                else -> throw IllegalArgumentException("Unknown Presenter class: $key")
            }
        } as P
    }

    @JvmStatic
    inline fun <reified P> obtain(): P {
        val presenterClass: Class<P> = P::class.java
        var presenter: ReactivePresenter<*>?
        for (key in presentersCache.keys) {
            presenter = presentersCache[key]
            if (presenterClass.isInstance(presenter)) {
                return presenterClass.cast(presenter)!!
            }
        }
        throw ClassCastException("${presenterClass.name} is Not Registered")
    }

    @JvmStatic
    fun destroy(presenterId: String) {
        presentersCache.remove(presenterId)
    }

}