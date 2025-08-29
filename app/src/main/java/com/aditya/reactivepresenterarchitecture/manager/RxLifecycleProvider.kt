package com.aditya.reactivepresenterarchitecture.manager

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import rx.Observable

class RxLifecycleProvider(
    private val lifecycle: Lifecycle
) {

    private val lifecycleObservable = RxLifecycleOwner.from(lifecycle).share()
    private val stopTransformer = RxLifecycleTransformer<Event>(
        lifecycleObservable.filter { event -> event === Event.ON_STOP }
    )
    private val destroyTransformer = RxLifecycleTransformer<Event>(
        lifecycleObservable.filter { event -> event === Event.ON_DESTROY }
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> bindUntilStop(): RxLifecycleTransformer<T> {
        return stopTransformer as RxLifecycleTransformer<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> bindUntilDestroy(): RxLifecycleTransformer<T> {
        return destroyTransformer as RxLifecycleTransformer<T>
    }

    fun lifecycleObservable(): Observable<Event> {
        return lifecycleObservable
    }

    fun addLifecycleObserver(observer: DefaultLifecycleObserver) {
        lifecycle.addObserver(observer)
    }

    protected fun finalize() {
        Log.d("ProfileTestActivity", "RxLifecycleProvider deleted from GC: ${this.hashCode()}")
    }
}
