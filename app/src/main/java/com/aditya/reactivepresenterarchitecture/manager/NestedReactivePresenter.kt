package com.aditya.reactivepresenterarchitecture.manager

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.manager.ReactivePresenter.Observer
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

abstract class NestedReactivePresenter<V, C>(
    state: V,
    private val componentStates: MutableMap<String, C>,
    lifecycle: Lifecycle, schedulerObserver: Scheduler
) : ReactivePresenter<V>(
    state, lifecycle, schedulerObserver
), INestedReactivePresenter<C> where V : ViewState<*>, C : ViewState<*> {

    private var subscriptionObserver: Subscription? = null
    private val componentSubscriptions = CompositeSubscription()
    private val _componentViewState: BehaviorSubject<Map<String, C>> = BehaviorSubject.create(
        componentStates.toMap()
    )
    private val componentViewState = _componentViewState.asObservable()
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

    fun getComponentViewState(): C? {
        return _componentViewState.value?.get(componentKey.get())
    }

    override fun observeComponentViewState(observer: Observer<C>) {
        subscriptionObserver?.unsubscribe()
        subscriptionObserver = componentViewState
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

    fun <T, R> bindComponentViewState(
        source: Observable<T>, loading: R?, success: Func1<T, R>?, error: Func1<Throwable, R>?,
    ) where R : C {
        componentSubscriptions.add(
            observersViewState(source, loading, success, error)
                .filter {
                    val isStart = !isComponentPaused.get()
                    it.getModelView().setDone(isStart)
                    isStart
                }
                .subscribe {
                    componentStates[componentKey.get()] = it
                    _componentViewState.onNext(componentStates.toMap())
                }
        )
    }

    override fun destroy() {
        super.destroy()
        subscriptionObserver?.unsubscribe()
        componentSubscriptions.unsubscribe()
    }

}

interface INestedReactivePresenter<C> where C : ViewState<*> {
    fun attachView(key: String)
    fun detachView()
    fun observeComponentViewState(observer: Observer<C>)
}
