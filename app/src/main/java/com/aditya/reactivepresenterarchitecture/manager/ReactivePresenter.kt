package com.aditya.reactivepresenterarchitecture.manager

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aditya.reactivepresenterarchitecture.ui.PresenterFactory
import rx.Observable
import rx.Scheduler
import rx.functions.Action1
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.atomic.AtomicBoolean

abstract class ReactivePresenter<V>(
    state: V, lifecycle: Lifecycle, protected val schedulerObserver: Scheduler
) where V : ViewState<*> {

    protected val lifecycleProvider = RxLifecycleProvider(lifecycle)
    protected val subscriptions = CompositeSubscription()
    private val _viewState: BehaviorSubject<V> = BehaviorSubject.create(state)
    private val viewState = _viewState.asObservable()
    private val isPaused = AtomicBoolean(false)

    init {
        lifecycleProvider.addLifecycleObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                isPaused.set(false)
            }

            override fun onPause(owner: LifecycleOwner) {
                stop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                if (owner is Activity && owner.isFinishing) destroy()
            }
        })
    }

    fun getViewState(): V {
        return _viewState.value
    }

    fun observeViewState(observer: Observer<V>) {
        subscriptions.add(
            viewState
                .distinctUntilChanged(this::validateNewValue)
                .compose(lifecycleProvider.bindUntilDestroy())
                .filter { !isPaused.get() }
                .observeOn(schedulerObserver)
                .subscribe {
                    it.getModelView().setDone(true)
                    observer.call(it)
                }
        )
    }

    open fun validateNewValue(
        oldState: ViewState<*>?, newState: ViewState<*>?
    ): Boolean {
        if (oldState == null || newState == null) return true
        return !newState.getModelView().isDone()
    }

    fun <T, R> bindViewState(
        source: Observable<T>, loading: R?, success: Func1<T, R>?, error: Func1<Throwable, R>?,
    ) where R : V {
        subscriptions.add(
            observersViewState(source, loading, success, error)
                .filter {
                    val isStart = !isPaused.get()
                    it.getModelView().setDone(isStart)
                    isStart
                }
                .subscribe {
                _viewState.onNext(it)
            }
        )
    }

    open fun <T, R> observersViewState(
        source: Observable<T>, loading: R?, success: Func1<T, R>?, error: Func1<Throwable, R>?
    ): Observable<R> where R : ViewState<*> {
        return source
            .map(success)
            .startWith(loading)
            .onErrorReturn {
                error?.call(
                    if (it?.message == null) Exception("Unknown error") else it
                )
            }
            .compose(lifecycleProvider.bindUntilStop())
            .observeOn(schedulerObserver)
    }

    open fun stop() {
        isPaused.set(true)
    }

    open fun destroy() {
        subscriptions.unsubscribe()
        PresenterFactory.destroy(this.javaClass.simpleName)
    }

    fun interface Observer<V> : Action1<V>

}