package com.aditya.reactivepresenterarchitecture.manager

fun interface PresenterInjector<P : ReactivePresenter<*>?> {
    /* inject presenter object from parent ui to child */
    fun obtainPresenter(presenterClass: Class<*>): P
}
