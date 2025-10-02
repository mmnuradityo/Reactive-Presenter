package com.aditya.reactivepresenterarchitecture.reactive_presenter.base

import androidx.lifecycle.Lifecycle
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.SchedulerProvider
import rx.Observable
import rx.Subscription
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicReference

abstract class CompositeReactivePresenter<VS, CS>(
    state: VS,
    private val componentStates: MutableMap<String, CS>,
    schedulerProvider: ISchedulerProvider = SchedulerProvider(),
) : ReactivePresenter<VS>(state, schedulerProvider),
    ICompositeReactivePresenter<CS> where VS : ViewState<*>, CS : ComponentViewState<*> {

    private var lifecycleComponentProvider: MutableMap<String, IRxLifecycleProvider> = mutableMapOf()
    private var subscriptionObserver: MutableMap<String, Subscription> = mutableMapOf()
    private val componentSubscriptions: MutableList<Pair<String, Subscription>> = mutableListOf()
    private val _componentState: BehaviorSubject<Map<String, CS>> = BehaviorSubject.create(
        componentStates.toMap()
    )
    private val componentState = _componentState.asObservable()
    private val isComponentPaused = AtomicReference(mutableMapOf<String, Boolean>())

    override fun getComponentState(key: String): CS? {
        return _componentState.value[key]
    }

    override fun emitComponentState(key: String, newViewState: CS) {
        if (key != newViewState.getComponentKey()) return
        componentStates[key] = newViewState
        _componentState.onNext(componentStates.toMap())
    }

    private fun resetEmitterComponentState(key: String) {
        val viewState = getComponentState(key)?.apply {
            getModelView().setConsume(false)
        }
        if (viewState != null) emitComponentState(key, viewState)
    }

    override fun observeComponentState(key: String, lifecycleProvider: IRxLifecycleProvider, observer: Observer<CS>) {
        subscriptionObserver[key]?.unsubscribe()
        setupLifecycleComponentProvider(key, lifecycleProvider)
        if (key.isNotEmpty()) attachView(key)

        val provider = lifecycleComponentProvider[key]
        if (provider == null) return

        subscriptionObserver[key] = componentState
            .compose(provider.bindUntilDestroy())
            .filter(validatePaused(key))
            .distinctUntilChanged { old, new -> validateConsumed(new[key]) }
            .map { it[key] }
            .observeOn(schedulerProvider.ui())
            .subscribe {
                if (it == null) return@subscribe
                observer.call(it)
            }
    }

    private fun setupLifecycleComponentProvider(key: String, lifecycleProvider: IRxLifecycleProvider) {
        this.lifecycleComponentProvider[key] = lifecycleProvider
        val eventKey = "${key}_event"
        subscriptionObserver[eventKey]?.unsubscribe()
        subscriptionObserver[eventKey] = lifecycleProvider.getLifecycleObservable()
            .observeOn(schedulerProvider.ui())
            .filter { validateOwner(it.owner) }
            .subscribe {
                if (it.event == Lifecycle.Event.ON_CREATE ) resetEmitterComponentState(key)
                else if (it.event == Lifecycle.Event.ON_DESTROY ) destroy()
            }
    }

    protected fun <T> bindComponentState(
        key: String, source: Observable<T>, success: Func1<T, CS>,
        loading: CS? = null, error: Func1<Throwable, CS>? = null
    ) {
       val provider = lifecycleComponentProvider[key]
        if (provider == null) return

        componentSubscriptions.add(
            Pair(
                key,
                transformViewState(source, success, loading, error)
                    .filter(validatePaused(key))
                    .map {
                        it?.setComponentKey(key)
                        it
                    }
                    .compose(provider.bindUntilPause())
                    .subscribe {
                        if (it == null) return@subscribe
                        emitComponentState(key, it)
                    }
            )
        )
    }

    fun <T> validatePaused(key: String) = Func1<T, Boolean> {
        !(isComponentPaused.get()[key] ?: true)
    }

    override fun attachView(key: String) {
        if (key.isNotEmpty()) isComponentPaused.get()[key] = false
    }

    override fun detachView(key: String) {
        if (key.isEmpty()) return
        componentSubscriptions.removeAll {
            if (it.first == key) {
                it.second.unsubscribe()
                return@removeAll true
            }
            false
        }
        isComponentPaused.get()[key] = true
    }

    override fun destroy() {
        super.destroy()
        subscriptionObserver.values.removeAll {
            it.unsubscribe()
            true
        }
        componentSubscriptions.removeAll {
            it.second.unsubscribe()
            true
        }
        lifecycleComponentProvider.values.removeAll {
            it.removeObserver()
            true
        }
    }

}

interface ICompositeReactivePresenter<CS>: IReactivePresenter where CS : ComponentViewState<*> {
    fun getComponentState(key: String): CS?
    fun emitComponentState(key: String, newViewState: CS)
    fun observeComponentState(key: String, lifecycleProvider: IRxLifecycleProvider, observer: Observer<CS>)
}
