package com.aditya.reactivepresenterarchitecture.reactive_presenter

object PresenterProvider {

    @JvmStatic
    fun <P> obtain(presenter: P?, syncClass: Any, presenterClass: Class<*>): P? {
        if (presenter == null) synchronized(syncClass) {
            if (presenter == null) throw ClassCastException("Presenter is Null")
        }
        if (presenterClass.isInstance(presenter)) return presenter
        val name = presenterClass.name
        throw ClassCastException(
            if (presenter != null) "$name is not an instance of " + presenter.javaClass.name
            else "$name is Null"
        )
    }

}

fun interface PresenterInjector<P : ReactivePresenter<*>?> {
    fun obtainPresenter(presenterClass: Class<*>): P
}
