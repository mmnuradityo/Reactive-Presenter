package com.aditya.reactivepresenterarchitecture.reactive_presenter.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.RxLifecycleEvent
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.RxLifecycleTransformer
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

abstract class BaseReactivePresenterTest<P> where P : IReactivePresenter{

    @MockK
    lateinit var mockSchedulerProvider: ISchedulerProvider
    @RelaxedMockK
    lateinit var mockLifecycleProvider: IRxLifecycleProvider
    @MockK
    lateinit var mockLifecycleOwner: LifecycleOwner
    @RelaxedMockK
    lateinit var fragment: Fragment

    protected val initialModel = TestModelView("initial")
    protected val initialViewState = TestViewState(model = initialModel)
    protected lateinit var lifecycleRegistry: LifecycleRegistry
    protected val lifecycleEventSubject = PublishSubject.create<RxLifecycleEvent>()
    protected val lifecycleEventObservable =  lifecycleEventSubject.asObservable()
    protected lateinit var presenter: P

    protected abstract fun onCreatePresenter(
        viewState: TestViewState, schedulerProvider: ISchedulerProvider
    ): P

    @BeforeEach
    open fun setUp() {
        MockKAnnotations.init(this)
        // Mock Schedulers
        every { mockSchedulerProvider.io() } returns Schedulers.trampoline()
        every { mockSchedulerProvider.ui() } returns Schedulers.trampoline()

        every { mockLifecycleProvider.getLifecycleObservable() } returns lifecycleEventObservable
        every { mockLifecycleProvider.removeObserver() } just Runs

        lifecycleRegistry = LifecycleRegistry(mockLifecycleOwner)
        every { mockLifecycleOwner.lifecycle } returns lifecycleRegistry

        presenter = onCreatePresenter(initialViewState, mockSchedulerProvider)
    }

    @AfterEach
    open fun tearDown() {
        clearAllMocks()
    }

    protected fun configureLifecycleProvider(lifecycleProvider: IRxLifecycleProvider = mockLifecycleProvider) {
        // Mock LifecycleProvider
        val subjectObserver = lifecycleProvider.getLifecycleObservable().asObservable()
        val identityTransformerStop = RxLifecycleTransformer<TestViewState>(
            subjectObserver.filter { it.event === Lifecycle.Event.ON_STOP }
        )
        val identityTransformerDestroy = RxLifecycleTransformer<TestViewState>(
            subjectObserver.filter { it.event === Lifecycle.Event.ON_DESTROY }
        )

        every { lifecycleProvider.bindUntilDestroy<TestViewState>() } returns identityTransformerStop
        every { lifecycleProvider.bindUntilPause<TestViewState>() } returns identityTransformerDestroy
    }

}