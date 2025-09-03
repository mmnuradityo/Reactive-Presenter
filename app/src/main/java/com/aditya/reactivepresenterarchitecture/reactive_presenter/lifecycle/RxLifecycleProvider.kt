package com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import rx.Observable

class RxLifecycleProvider(lifecycle: Lifecycle) {

    private val _lifecycleObservable = RxLifecycleOwner.from(lifecycle).share()
    val lifecycleObservable: Observable<RxLifecycleEvent> = _lifecycleObservable.asObservable()
    private val stopTransformer = RxLifecycleTransformer<Event>(
        _lifecycleObservable.filter { it.event === Event.ON_STOP }
    )
    private val destroyTransformer = RxLifecycleTransformer<Event>(
        _lifecycleObservable.filter { it.event === Event.ON_DESTROY }
    )
    private val pauseTransformer = RxLifecycleTransformer<Event>(
        _lifecycleObservable.filter { it.event === Event.ON_PAUSE }
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> bindUntilStop(): RxLifecycleTransformer<T> {
        return stopTransformer as RxLifecycleTransformer<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> bindUntilDestroy(): RxLifecycleTransformer<T> {
        return destroyTransformer as RxLifecycleTransformer<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> bindUntilPause(): RxLifecycleTransformer<T> {
        return pauseTransformer as RxLifecycleTransformer<T>
    }

}
