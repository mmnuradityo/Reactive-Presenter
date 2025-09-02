package com.aditya.reactivepresenterarchitecture.reactive_presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import rx.Observable
import rx.subjects.PublishSubject


object RxLifecycleOwner {

    fun from(lifecycle: Lifecycle): Observable<Event> {
        val subject = PublishSubject.create<Event>()
        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                subject.onNext(event)
            })
        return subject.asObservable()
    }

}