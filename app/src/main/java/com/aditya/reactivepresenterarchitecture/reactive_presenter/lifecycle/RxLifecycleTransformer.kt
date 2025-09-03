package com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle

import rx.Observable

class RxLifecycleTransformer<T>(
    private val stopSignal: Observable<RxLifecycleEvent>
) : Observable.Transformer<T, T> {

    override fun call(source: Observable<T>): Observable<T> {
        return source.takeUntil(stopSignal)
    }

}