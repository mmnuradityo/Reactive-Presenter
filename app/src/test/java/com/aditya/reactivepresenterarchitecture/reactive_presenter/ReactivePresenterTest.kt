package com.aditya.reactivepresenterarchitecture.reactive_presenter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle.Event
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.BaseReactivePresenterTest
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestableReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.RxLifecycleEvent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import rx.Observable
import rx.subjects.PublishSubject

class ReactivePresenterTest : BaseReactivePresenterTest<TestableReactivePresenter>() {

    override fun onCreatePresenter(
        viewState: TestViewState, schedulerProvider: ISchedulerProvider,
    ) = TestableReactivePresenter(viewState, schedulerProvider)


    @Test
    fun `test validateOwner when lifecycle owner Fragment is visible`() = with(fragment) {
        every { activity } returns null
        every { isAdded } returns true
        every { isVisible } returns true
        val isValid = presenter.validateOwner(this)
        assertTrue(isValid)
    }

    @Test
    fun `test validateOwner when lifecycle owner Fragment is not addes`() = with(fragment) {
        every { activity } returns null
        every { isAdded } returns false
        every { isVisible } returns true
        val isValid = presenter.validateOwner(this)
        assertFalse(isValid)
    }

    @Test
    fun `test validateOwner when lifecycle owner Fragment is added and is not visible`() =
        with(fragment) {
            every { activity } returns null
            every { isAdded } returns true
            every { isVisible } returns false

            val isValid = presenter.validateOwner(this)
            assertFalse(isValid)
        }

    @Test
    fun `test validateOwner when lifecycle owner Fragment is remove`() = with(fragment) {
        presenter.observeViewState(mockLifecycleProvider) { }

        val parentActivity = mockk<FragmentActivity>()
        every { parentActivity.isFinishing } returns false
        every { activity } returns parentActivity
        every { isRemoving } returns true

        val isValid = presenter.validateOwner(this)
        assertFalse(isValid)
    }

    @Test
    fun `test validateOwner when lifecycle owner Fragment with parent activity on destroy state`() =
        with(fragment) {
            presenter.observeViewState(mockLifecycleProvider) { }

            val parentActivity = mockk<FragmentActivity>()
            every { parentActivity.isFinishing } returns true
            every { activity } returns parentActivity

            val isValid = presenter.validateOwner(this)
            assertFalse(isValid)
        }

    @Test
    fun `test validateOwner when lifecycle is not Fragment`() {
        val isValid = presenter.validateOwner(mockLifecycleOwner)
        assertTrue(isValid)
    }

    @Test
    fun `test getViewState when viewState is valid`() {
        val resultViewState = presenter.getViewState()
        assertTrue { resultViewState == initialViewState }
    }

    @Test
    fun `test validateConsumed when value is not consumed`() {
        val newModel = TestModelView("new")
        newModel.setConsume(false)
        val isConsumed = presenter.testableValidateConsumed(
            TestViewState(model = newModel)
        )
        assertFalse(isConsumed)
    }

    @Test
    fun `test validateConsumed when value is consumed`() {
        val newModel = TestModelView("new")
        newModel.setConsume(true)
        val isConsumed = presenter.testableValidateConsumed(
            TestViewState(model = newModel)
        )
        assertTrue(isConsumed)
    }

    @Test
    fun `test validateConsumed when newViewState is null`() {
        val isConsumed = presenter.testableValidateConsumed( null)
        assertTrue(isConsumed)
    }

    @Test
    fun `test bindViewState when success`() {
        configureLifecycleProvider()

        val loadingId = 1
        val successId = 2
        val expectationLoading = "Loading"
        val expectationResult = "success"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            source = Observable.just(expectationResult),
            success = { TestViewState(successId, TestModelView(it)) },
            loading = TestViewState(loadingId, TestModelView(expectationLoading)),
            error = { null }
        )
    }

    @Test
    fun `test bindViewState when success null`() {
        configureLifecycleProvider()

        val loadingId = 1
        val successId = 2
        val expectationLoading = "Loading"
        val expectationResult = "success"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewStateWithoutSuccess(
            source = Observable.just(expectationResult),
            loading = TestViewState(loadingId, TestModelView(expectationLoading)),
            error = { null }
        )
    }

    @Test
    fun `test bindViewState when success return null value`() {
        configureLifecycleProvider()

        val loadingId = 1
        val successId = 2
        val expectationLoading = "Loading"
        val expectationResult = "success"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            source = Observable.just(expectationResult),
            success = { null },
            loading = TestViewState(loadingId, TestModelView(expectationLoading)),
            error = { null }
        )
    }

    @Test
    fun `test bindViewState when error`() {
        configureLifecycleProvider()

        val loadingId = 1
        val successId = 2
        val errorId = 3
        val expectationLoading = "Loading"
        val expectationResult = "success"
        val expectationError = "Error"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                errorId -> assertEquals(
                    expectationError, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            source = Observable.error(Throwable(expectationError)),
            success = { TestViewState(successId, TestModelView(it)) },
            loading = TestViewState(loadingId, TestModelView(expectationLoading)),
            error = { TestViewState(errorId, TestModelView(it.message!!)) }
        )
    }

    @Test
    fun `test bindViewState when error but error is null`() {
        configureLifecycleProvider()

        val successId = 2
        val errorId = 3
        val expectationResult = "success"
        val expectationError = "Unknown error"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                errorId -> assertEquals(
                    expectationError, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewStateWithoutLoadingAndError(
            source = Observable.error(Throwable(expectationError)),
            success = { TestViewState(successId, TestModelView(it)) },
        )
    }

    @Test
    fun `test bindViewState when error but error message is null`() {
        configureLifecycleProvider()

        val successId = 2
        val errorId = 3
        val expectationResult = "success"
        val expectationError = "Unknown error"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                errorId -> assertEquals(
                    expectationError, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            source = Observable.error(Throwable()),
            success = { TestViewState(successId, TestModelView(it)) },
            loading = null,
            error = { error ->
                error.message?.let { TestViewState(errorId, TestModelView(it)) }
            }
        )
    }

    @Test
    fun `test bindViewState when error submit null`() {
        configureLifecycleProvider()

        val successId = 2
        val errorId = 3
        val expectationResult = "success"
        val expectationError = "Unknown error"

        presenter.observeViewState(mockLifecycleProvider) {
            when (it.getId()) {
                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                errorId -> assertEquals(
                    expectationError, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            source = Observable.error(null),
            success = { TestViewState(successId, TestModelView(it)) },
            loading = null,
            error = { error ->
                error.message?.let { TestViewState(errorId, TestModelView(it)) }
            }
        )
    }

    @Test
    fun `test observeViewState when lifecycle is ON_RESUME`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_RESUME))
    }

    @Test
    fun `test observeViewState when lifecycle is ON_PAUSE`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_PAUSE))
    }

    @Test
    fun `test observeViewState when lifecycle is ON_DESTROY`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_DESTROY))
    }


    @Test
    fun `test observeViewState when lifecycle is not handled`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_ANY))
    }

    @Test
    fun `test observeViewState when lifecycle is ON_DESTROY Activity`() {
        observeOwnerActivityDestroy()
    }

    @Test
    fun `test observeViewState when lifecycle is ON_DESTROY Finish Activity`() {
        observeOwnerActivityDestroy(true)
    }

    private fun observeOwnerActivityDestroy(isFinishing: Boolean = false) {
        val activity = mockk<FragmentActivity>()
        every { activity.lifecycle } returns lifecycleRegistry
        every { activity.isFinishing } returns isFinishing

        val lifecycleSubject = PublishSubject.create<RxLifecycleEvent>()
        val lifecycleObservable = lifecycleSubject.asObservable()

        val lifecycleProvider = mockk<IRxLifecycleProvider>()
        every { lifecycleProvider.getLifecycleObservable() } returns lifecycleObservable
        every { lifecycleProvider.removeObserver() } just Runs

        configureLifecycleProvider(lifecycleProvider)
        runFullObserve(lifecycleProvider)
        lifecycleSubject.onNext(RxLifecycleEvent(activity, Event.ON_DESTROY))
    }

    @Test
    fun `test observeViewState when lifecycle is Another`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_CREATE))
    }

    private fun runFullObserve(lifecycleProvider: IRxLifecycleProvider = mockLifecycleProvider) {
        configureLifecycleProvider()

        val loadingId = 1
        val successId = 2
        val errorId = 3
        val expectationLoading = "Loading"
        val expectationResult = "success"
        val expectationError = "Error"

        presenter.observeViewState(lifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                successId -> assertEquals(
                    expectationResult, it.getModelView().get()
                )

                errorId -> assertEquals(
                    expectationError, it.getModelView().get()
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            source = Observable.just(expectationResult),
            success = { TestViewState(successId, TestModelView(it)) },
            loading = TestViewState(loadingId, TestModelView(expectationLoading)),
            error = { TestViewState(errorId, TestModelView(it.message!!)) }
        )
    }

}