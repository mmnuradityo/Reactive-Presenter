package com.aditya.reactivepresenterarchitecture.manager

import rx.Observable

class RxLifecycleTransformer<T>(
    private val stopSignal: Observable<T>
) : Observable.Transformer<T, T> {

    override fun call(source: Observable<T>): Observable<T> {
        return source.takeUntil(stopSignal)
    }

}