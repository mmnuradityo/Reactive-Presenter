package com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import rx.Observable
import rx.subjects.PublishSubject

class RxLifecycleProvider(private val lifecycle: Lifecycle): IRxLifecycleProvider {

    private val subject = PublishSubject.create<RxLifecycleEvent>()
    private val observer = RxLifecycleOwner.observer(subject)
    private val _lifecycleObservable = RxLifecycleOwner.from(subject, lifecycle, observer).share()

    private val stopTransformer = RxLifecycleTransformer<Event>(
        _lifecycleObservable.filter { it.event === Event.ON_STOP }
    )
    private val destroyTransformer = RxLifecycleTransformer<Event>(
        _lifecycleObservable.filter { it.event === Event.ON_DESTROY }
    )
    private val pauseTransformer = RxLifecycleTransformer<Event>(
        _lifecycleObservable.filter { it.event === Event.ON_PAUSE }
    )

    override fun getLifecycleObservable(): Observable<RxLifecycleEvent> = _lifecycleObservable.asObservable()
    @Suppress("UNCHECKED_CAST")
    override fun <T> bindUntilStop(): RxLifecycleTransformer<T> {
        return stopTransformer as RxLifecycleTransformer<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> bindUntilDestroy(): RxLifecycleTransformer<T> {
        return destroyTransformer as RxLifecycleTransformer<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> bindUntilPause(): RxLifecycleTransformer<T> {
        return pauseTransformer as RxLifecycleTransformer<T>
    }

    override fun removeObserver() {
        lifecycle.removeObserver(observer)
    }
}

interface IRxLifecycleProvider {
    fun getLifecycleObservable(): Observable<RxLifecycleEvent>
    fun <T> bindUntilPause(): RxLifecycleTransformer<T>
    fun <T> bindUntilStop(): RxLifecycleTransformer<T>
    fun<T> bindUntilDestroy(): RxLifecycleTransformer<T>
    fun removeObserver()
}