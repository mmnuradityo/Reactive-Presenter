package com.aditya.reactivepresenterarchitecture.ui

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.manager.ReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.main.MainPresenter
import rx.android.schedulers.AndroidSchedulers

object PresenterFactory {

    val presentersCache = mutableMapOf<String, ReactivePresenter<*>>()

    @JvmStatic
    inline fun <reified P> create(lifecycle: Lifecycle): P where P : ReactivePresenter<*> {
        return presentersCache.getOrPut(P::class.java.simpleName) {
            MainPresenter(lifecycle, AndroidSchedulers.mainThread())
        } as P
    }

    @JvmStatic
    fun destroy(presenterId: String) {
        presentersCache.remove(presenterId)
    }

}