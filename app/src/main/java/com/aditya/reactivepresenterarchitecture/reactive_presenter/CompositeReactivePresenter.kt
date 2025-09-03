package com.aditya.reactivepresenterarchitecture.reactive_presenter

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
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
    schedulerObserver: Scheduler,
) : ReactivePresenter<VS>(
    state, schedulerObserver
), ICompositeReactivePresenter<CS> where VS : ViewState<*>, CS : ComponentViewState<*> {

    private var lifecycleComponentProvider: MutableMap<String, RxLifecycleProvider> = mutableMapOf()
    private var subscriptionObserver: MutableMap<String, Subscription> = mutableMapOf()
    private val componentSubscriptions = CompositeSubscription()
    private val _componentState: BehaviorSubject<Map<String, CS>> = BehaviorSubject.create(
        componentStates.toMap()
    )
    private val componentState = _componentState.asObservable()
    private val isComponentPaused = AtomicBoolean(false)
    protected val componentKey = AtomicReference("")

    override fun getComponentState(): CS? {
        return _componentState.value?.get(componentKey.get())
    }

    override fun observeComponentState(key: String, lifecycle: Lifecycle, observer: Observer<CS>) {
        subscriptionObserver[key]?.unsubscribe()
        lifecycleComponentProvider[key] = lifecycleComponentProvider(key, lifecycle)
        attachView(key)

        subscriptionObserver[key] = componentState
            .distinctUntilChanged { old, new ->
               key != componentKey.get() || validateNewValue(old[key], new[key])
            }
            .map { it[key] }
            .filter { !isComponentPaused.get() && it != null }
            .compose(lifecycleComponentProvider[key]?.bindUntilDestroy())
            .observeOn(schedulerObserver)
            .subscribe {
                if (it == null) return@subscribe
                observer.call(it)
                it.getModelView().setConsume(true)
            }
    }

    private fun lifecycleComponentProvider(key: String, lifecycle: Lifecycle): RxLifecycleProvider {
        return RxLifecycleProvider(lifecycle).apply {
            val eventKey = "${key}_event"
            subscriptionObserver[eventKey]?.unsubscribe()
            subscriptionObserver[eventKey] = lifecycleObservable
                .filter { validateOwner(it.owner) }
                .subscribe {
                    when (it.event) {
                        Lifecycle.Event.ON_RESUME -> attachView(key)
                        Lifecycle.Event.ON_PAUSE -> detachView()
                        else -> { /* ignored */ }
                    }
                }
        }
    }

    private fun validateOwner(owner: LifecycleOwner): Boolean {
        if (owner is Fragment) {
            if (owner.isHidden || !owner.isVisible || !owner.isAdded || owner.isRemoving) {
                return false
            }
        }
        return true
    }

    protected fun <T> bindComponentState(
        source: Observable<T>, success: Func1<T, CS>?, loading: CS?, error: Func1<Throwable, CS>?
    ) {
        val key = componentKey.get()
        componentSubscriptions.add(
            transformViewState(source, success, loading, error)
                .map {
                    it.apply {
                        setComponentKey(key)
                        getModelView().setConsume(false)
                    }
                }
                .filter { !isComponentPaused.get() && it.getComponentKey() == componentKey.get() }
                .compose(lifecycleComponentProvider[componentKey.get()]?.bindUntilPause())
                .subscribe {
                    componentStates[it.getComponentKey()] = it
                    _componentState.onNext(componentStates.toMap())
                }
        )
    }

    override fun attachView(key: String) {
        componentKey.set(key)
        isComponentPaused.set(false)
    }

    override fun detachView() {
        componentSubscriptions.clear()
        isComponentPaused.set(true)
    }

    override fun destroy() {
        super.destroy()
        subscriptionObserver.values.forEach { it.unsubscribe() }
        subscriptionObserver.clear()
        lifecycleComponentProvider.clear()
        componentSubscriptions.unsubscribe()
    }

}

interface ICompositeReactivePresenter<CS> where CS : ComponentViewState<*> {
    fun attachView(key: String)
    fun detachView()
    fun getComponentState(): CS?
    fun observeComponentState(key: String, lifecycle: Lifecycle, observer: Observer<CS>)
}
