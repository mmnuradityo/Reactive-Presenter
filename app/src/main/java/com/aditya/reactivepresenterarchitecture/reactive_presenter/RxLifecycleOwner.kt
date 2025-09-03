package com.aditya.reactivepresenterarchitecture.reactive_presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import rx.Observable
import rx.subjects.PublishSubject


object RxLifecycleOwner {

    fun from(lifecycle: Lifecycle): Observable<RxLifecycleEvent> {
        val subject = PublishSubject.create<RxLifecycleEvent>()
        var result: RxLifecycleEvent? = null
        lifecycle.addObserver(
            LifecycleEventObserver { owner, event ->
                if (result != null) result!!.event = event
                else result = RxLifecycleEvent(owner, event)
                subject.onNext(result!!)
            })
        return subject.asObservable()
    }

}

data class RxLifecycleEvent(
    val owner: LifecycleOwner,
    var event: Event
)