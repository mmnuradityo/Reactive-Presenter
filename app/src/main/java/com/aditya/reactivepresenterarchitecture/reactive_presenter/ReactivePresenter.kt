package com.aditya.reactivepresenterarchitecture.reactive_presenter

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

abstract class ReactivePresenter<VS>(
    state: VS, lifecycle: Lifecycle, protected val schedulerObserver: Scheduler
) where VS : ViewState<*> {

    protected val lifecycleProvider = RxLifecycleProvider(lifecycle)
    protected val subscriptions = CompositeSubscription()
    private val _viewState: BehaviorSubject<VS> = BehaviorSubject.create(state)
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

    fun getViewState(): VS {
        return _viewState.value
    }

    fun observeViewState(observer: Observer<VS>) {
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

    protected  open fun validateNewValue(
        oldState: ViewState<*>?, newState: ViewState<*>?
    ): Boolean {
        if (oldState == null || newState == null) return true
        return !newState.getModelView().isDone()
    }

    protected fun <T> bindViewState(
        source: Observable<T>, loading: VS?, success: Func1<T, VS>?, error: Func1<Throwable, VS>?,
    ) {
        subscriptions.add(
            transformViewState(source, loading, success, error)
                .filter {
                    val isStart = !isPaused.get()
                    it.getModelView().setDone(isStart)
                    isStart
                }
                .subscribe { _viewState.onNext(it) }
        )
    }

    protected open fun <T, R> transformViewState(
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

    protected open fun stop() {
        isPaused.set(true)
    }

    protected open fun destroy() {
        subscriptions.unsubscribe()
        PresenterFactory.destroy(this.javaClass.simpleName)
    }

}

fun interface Observer<VS> : Action1<VS> where VS : ViewState<*>
