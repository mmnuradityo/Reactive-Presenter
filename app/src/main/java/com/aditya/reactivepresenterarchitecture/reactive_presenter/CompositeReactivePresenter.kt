package com.aditya.reactivepresenterarchitecture.reactive_presenter

import androidx.lifecycle.Lifecycle
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

abstract class CompositeReactivePresenter<VS, CS>(
    state: VS,
    private val componentStates: MutableMap<String, CS>,
    lifecycle: Lifecycle, schedulerObserver: Scheduler
) : ReactivePresenter<VS>(
    state, lifecycle, schedulerObserver
), ICompositeReactivePresenter<CS> where VS : ViewState<*>, CS : ViewState<*> {

    private var subscriptionObserver: Subscription? = null
    private val componentSubscriptions = CompositeSubscription()
    private val _componentState: BehaviorSubject<Map<String, CS>> = BehaviorSubject.create(
        componentStates.toMap()
    )
    private val componentState = _componentState.asObservable()
    private val isComponentPaused = AtomicBoolean(false)
    protected val componentKey = AtomicReference("")

    override fun attachView(key: String) {
        componentKey.set(key)
        isComponentPaused.set(false)
    }

    override fun detachView() {
        componentKey.set("")
        componentSubscriptions.clear()
        isComponentPaused.set(true)
    }

    override fun getComponentState(): CS? {
        return _componentState.value?.get(componentKey.get())
    }

    override fun observeComponentState(observer: Observer<CS>) {
        subscriptionObserver?.unsubscribe()
        subscriptionObserver = componentState
            .distinctUntilChanged { old, new ->
                val key = componentKey.get()
                val isValid = validateNewValue(old[key], new[key])
                return@distinctUntilChanged isValid
            }
            .map { it[componentKey.get()] }
            .compose(lifecycleProvider.bindUntilDestroy())
            .filter { !isComponentPaused.get() }
            .observeOn(schedulerObserver)
            .subscribe {
                if (it == null) return@subscribe

                it.getModelView().setDone(true)
                observer.call(it)
            }
    }

    protected fun <T> bindComponentState(
        source: Observable<T>, loading: CS?, success: Func1<T, CS>?, error: Func1<Throwable, CS>?,
    ) {
        componentSubscriptions.add(
            transformViewState(source, loading, success, error)
                .filter {
                    val isStart = !isComponentPaused.get()
                    it.getModelView().setDone(isStart)
                    isStart
                }
                .subscribe {
                    componentStates[componentKey.get()] = it
                    _componentState.onNext(componentStates.toMap())
                }
        )
    }

    override fun destroy() {
        super.destroy()
        subscriptionObserver?.unsubscribe()
        componentSubscriptions.unsubscribe()
    }

}

interface ICompositeReactivePresenter<CS> where CS : ViewState<*> {
    fun attachView(key: String)
    fun detachView()
    fun getComponentState(): CS?
    fun observeComponentState(observer: Observer<CS>)
}
