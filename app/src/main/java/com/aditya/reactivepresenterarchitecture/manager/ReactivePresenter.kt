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


abstract class ReactivePresenter<V>(
    state: V, lifecycle: Lifecycle, private val schedulerObserver: Scheduler,
) where V : ViewState {

    private val lifecycleProvider = RxLifecycleProvider(lifecycle)
    private val subscriptions = CompositeSubscription()
    private val _viewState: BehaviorSubject<V> = BehaviorSubject.create(state)
    private val viewState = _viewState.asObservable()

    init {
        lifecycleProvider.addLifecycleObserver(object : DefaultLifecycleObserver {
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
                .distinctUntilChanged()
                .compose(lifecycleProvider.bindUntilStop())
                .observeOn(schedulerObserver)
                .subscribe(observer)
        )
    }

    fun <T, R> transformAndSubscribe(
        source: Observable<T>, loading: R?, success: Func1<T, R>?, error: Func1<Throwable, R>?,
    ) where R : V {
        subscriptions.add(
            source
                .compose(lifecycleProvider.bindUntilStop())
                .map(success)
                .onErrorReturn {
                    error?.call(
                        if (it?.message == null) Exception("Unknown error") else it
                    )
                }
                .startWith(loading)
                .observeOn(schedulerObserver)
                .subscribe { _viewState.onNext(it) }
        )
    }

    fun destroy() {
        subscriptions.unsubscribe()
        PresenterFactory.destroy(this.javaClass.simpleName)
    }

    fun interface Observer<V> : Action1<V>

}