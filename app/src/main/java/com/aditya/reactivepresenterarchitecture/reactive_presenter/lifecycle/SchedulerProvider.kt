package com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle

import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SchedulerProvider: ISchedulerProvider {
    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    override fun io(): Scheduler = Schedulers.io()
}

interface ISchedulerProvider {
    fun ui(): Scheduler
    fun io(): Scheduler
}