package com.aditya.reactivepresenterarchitecture.reactive_presenter

import android.app.Activity
import androidx.lifecycle.Lifecycle
import rx.Observable
import rx.Scheduler
import rx.functions.Action1
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.atomic.AtomicBoolean

abstract class ReactivePresenter<VS>(
    state: VS, protected val schedulerObserver: Scheduler
) where VS : ViewState<*> {

    private val subscriptions = CompositeSubscription()
    private lateinit var lifecycleProvider: RxLifecycleProvider
    private val _viewState: BehaviorSubject<VS> = BehaviorSubject.create(state)
    private val viewState = _viewState.asObservable()
    private val isPaused = AtomicBoolean(false)

    fun getViewState(): VS {
        return _viewState.value
    }

    fun observeViewState(lifecycle: Lifecycle, observer: Observer<VS>) {
        subscriptions.clear()
        lifecycleProvider = lifecycleProvider(lifecycle)
        subscriptions.add(
            viewState
                .distinctUntilChanged(this::validateNewValue)
                .compose(lifecycleProvider.bindUntilDestroy())
                .filter { !isPaused.get() }
                .observeOn(schedulerObserver)
                .subscribe {
                    it.getModelView().setConsume(true)
                    observer.call(it)
                }
        )
    }

    private fun lifecycleProvider(lifecycle: Lifecycle): RxLifecycleProvider {
        return RxLifecycleProvider(lifecycle).apply {
            subscriptions.add(lifecycleObservable.subscribe {
                when (it.event) {
                    Lifecycle.Event.ON_RESUME -> isPaused.set(false)
                    Lifecycle.Event.ON_PAUSE -> isPaused.set(true)
                    Lifecycle.Event.ON_DESTROY -> {
                        val owner = it.owner
                        if (owner is Activity && owner.isFinishing) destroy()
                    }

                    else -> { /* ignored */
                    }
                }
            })
        }
    }

    protected  open fun validateNewValue(
        oldState: ViewState<*>?, newState: ViewState<*>?
    ): Boolean {
        return if (newState == null) true
        else if (oldState != null && oldState == newState) !newState.getModelView().isConsume()
        else false
    }

    protected fun <T> bindViewState(
        source: Observable<T>, success: Func1<T, VS>?, loading: VS?, error: Func1<Throwable, VS>?,
    ) {
        subscriptions.add(
            transformViewState(source, success, loading, error)
                .filter {
                    val isStart = !isPaused.get()
                    it.getModelView().setConsume(isStart)
                    isStart
                }
                .compose(lifecycleProvider.bindUntilPause())
                .subscribe { _viewState.onNext(it) }
        )
    }

    protected open fun <T, R> transformViewState(
        source: Observable<T>, success: Func1<T, R>?, loading: R?,  error: Func1<Throwable, R>?
    ): Observable<R> where R : ViewState<*> {
        return source
            .map(success)
            .startWith(loading)
            .onErrorReturn {
                error?.call(
                    if (it?.message == null) Exception("Unknown error") else it
                )
            }
            .observeOn(schedulerObserver)
    }

    protected open fun destroy() {
        subscriptions.unsubscribe()
        PresenterFactory.destroy(this.javaClass.simpleName)
    }

}

fun interface Observer<VS> : Action1<VS> where VS : ViewState<*>
