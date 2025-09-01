package com.aditya.reactivepresenterarchitecture.ui

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.manager.ReactivePresenter
import com.aditya.reactivepresenterarchitecture.ui.main.MainPresenter
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentPresenter
import rx.android.schedulers.AndroidSchedulers

object PresenterFactory {

    val presentersCache = mutableMapOf<String, ReactivePresenter<*>>()

    @JvmStatic
    inline fun <reified P> create(lifecycle: Lifecycle): P where P : ReactivePresenter<*> {
        val key = P::class.java.simpleName
        return presentersCache.getOrPut(P::class.java.simpleName) {
            when (key) {
                MainPresenter::class.java.simpleName -> MainPresenter(
                    lifecycle, AndroidSchedulers.mainThread()
                )
                MainFragmentPresenter::class.java.simpleName -> MainFragmentPresenter(
                    lifecycle, AndroidSchedulers.mainThread()
                )
                else -> throw IllegalArgumentException("Unknown Presenter class: $key")
            }
        } as P
    }

    @JvmStatic
    fun destroy(presenterId: String) {
        presentersCache.remove(presenterId)
    }

}