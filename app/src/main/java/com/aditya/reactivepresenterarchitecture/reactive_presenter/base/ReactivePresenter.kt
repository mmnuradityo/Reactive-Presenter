package com.aditya.reactivepresenterarchitecture.reactive_presenter.base

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.SchedulerProvider
import rx.Observable
import rx.functions.Action1
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.atomic.AtomicBoolean

abstract class ReactivePresenter<VS>(
    state: VS, protected val schedulerProvider: ISchedulerProvider = SchedulerProvider()
): IReactivePresenter where VS : ViewState<*> {

    private val subscriptions = CompositeSubscription()
    private lateinit var lifecycleProvider: IRxLifecycleProvider
    private val _viewState: BehaviorSubject<VS> = BehaviorSubject.create(state)
    private val viewState = _viewState.asObservable()
    private val isPaused = AtomicBoolean(false)

    fun getViewState(): VS {
        return _viewState.value
    }

    fun emitVewState(newViewState: VS?) {
        _viewState.onNext(newViewState)
    }

    private fun resetEmitter() {
        val viewState = _viewState.value?.apply {
            getModelView().setConsume(false)
        }
        if (viewState != null) emitVewState(viewState)
    }

    fun observeViewState(lifecycleProvider: IRxLifecycleProvider, observer: Observer<VS>) {
        subscriptions.clear()
        setupLifecycleProvider(lifecycleProvider)
        subscriptions.add(
            viewState
                .filter { !isPaused.get() && it != null && !it.getModelView().isConsume() }
                .distinctUntilChanged { old, new -> validateSameValue(new) }
                .compose(lifecycleProvider.bindUntilDestroy())
                .observeOn(schedulerProvider.ui())
                .subscribe {
                    observer.call(it)
                }
        )
    }

    private fun setupLifecycleProvider(lifecycleProvider: IRxLifecycleProvider) {
        this.lifecycleProvider = lifecycleProvider
        subscriptions.add(
            lifecycleProvider.getLifecycleObservable()
                .observeOn(schedulerProvider.ui())
                .filter { validateOwner(it.owner) }
                .subscribe {
                    when (it.event) {
                        Lifecycle.Event.ON_CREATE -> resetEmitter()
                        Lifecycle.Event.ON_RESUME -> isPaused.set(false)
                        Lifecycle.Event.ON_PAUSE -> isPaused.set(true)
                        Lifecycle.Event.ON_DESTROY -> {
                            val owner = it.owner
                            if (owner is Activity && owner.isFinishing) destroy()
                        }
                        else -> { /* ignored */ }
                    }
                })
    }

    open fun validateOwner(owner: LifecycleOwner): Boolean {
        if (owner !is Fragment) return true
        val isActivityFinishing = owner.activity?.isFinishing == true
        if (owner.isRemoving || isActivityFinishing) {
            destroy()
            return false
        }
        if (!owner.isAdded || !owner.isVisible) return false
        return true
    }

    protected open fun validateSameValue(newState: ViewState<*>?): Boolean {
        return newState?.getModelView()?.isConsume() ?: true
    }

    protected fun <T> bindViewState(
        source: Observable<T>, success: Func1<T, VS>? = null, loading: VS? = null, error: Func1<Throwable, VS>? = null
    ) {
        subscriptions.add(
            transformViewState(source, success, loading, error)
                .filter { !isPaused.get() }
                .compose(lifecycleProvider.bindUntilPause())
                .subscribe(this::emitVewState)
        )
    }

    protected open fun <T, R> transformViewState(
        source: Observable<T>, success: Func1<T, R>?, loading: R?,  error: Func1<Throwable, R>?
    ): Observable<R> where R : ViewState<*> {
        return source
            .subscribeOn(schedulerProvider.io())
            .map {
                @Suppress("UNCHECKED_CAST")
                success?.call(it) ?: it as R
            }
            .startWith(loading)
            .onErrorReturn {
                error?.call(
                    if (it == null || it.message == null) Exception("Unknown error")
                    else it
                )
            }
            .doOnNext {
                if (it == null) return@doOnNext
                it.getModelView().setConsume(false)
            }
    }

    protected open fun destroy() {
        subscriptions.unsubscribe()
        lifecycleProvider.removeObserver()
        PresenterFactory.destroy(this.javaClass.simpleName)
    }

}

interface IReactivePresenter {
    fun attachView(key: String) { }
    fun detachView(key: String) { }
}

fun interface Observer<VS> : Action1<VS> where VS : ViewState<*>
