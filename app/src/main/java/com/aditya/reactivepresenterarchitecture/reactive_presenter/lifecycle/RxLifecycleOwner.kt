package com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import rx.Observable
import rx.subjects.PublishSubject

object RxLifecycleOwner {

    @JvmStatic
    fun observer(
        subject: PublishSubject<RxLifecycleEvent>
    ): LifecycleObserver {
        var result: RxLifecycleEvent? = null
        return LifecycleEventObserver { owner, event ->
            if (result != null) result!!.event = event
            else result = RxLifecycleEvent(owner, event)
            subject.onNext(result!!)
        }
    }

    @JvmStatic
    fun from(
        subject: PublishSubject<RxLifecycleEvent>,
        lifecycle: Lifecycle, observer: LifecycleObserver
    ): Observable<RxLifecycleEvent> {
        lifecycle.addObserver(observer)
        return subject.asObservable()
    }

}

data class RxLifecycleEvent(
    val owner: LifecycleOwner,
    var event: Event
)