package com.aditya.reactivepresenterarchitecture.manager

object PresenterProvider {

    @JvmStatic
    fun <P> obtain(presenter: P?, syncClass: Any, presenterClass: Class<*>): P? {
        if (presenter == null) {
            synchronized(syncClass) {
                if (presenter == null) throw ClassCastException("Presenter is Null")
            }
        }

        if (presenterClass.isInstance(presenter)) return presenter

        throw ClassCastException(
            if (presenter != null)
                presenterClass.name + " is not an instance of " + presenter.javaClass.name
            else presenterClass.name + " is Null"
        )
    }

}