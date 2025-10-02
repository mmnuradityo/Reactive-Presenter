package com.aditya.reactivepresenterarchitecture.reactive_presenter

import androidx.lifecycle.Lifecycle.Event
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.BaseCompositePresenterTest
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TEST_COMPONENT_KEY
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TEST_NOT_FOUND_COMPONENT_KEY
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestChildViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestModelView
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestViewState
import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestableCompositePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.ISchedulerProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.RxLifecycleEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import rx.Observable

class CompositePresenterTest : BaseCompositePresenterTest<TestableCompositePresenter>(
    TEST_COMPONENT_KEY
) {

    override fun onCreatePresenter(
        viewState: TestViewState,
        schedulerProvider: ISchedulerProvider,
    ) = TestableCompositePresenter(viewState, schedulerProvider)

    @Test
    fun `test bindViewState when success return null value`() {
        configureLifecycleProvider()

        val loadingId = 1
        val errorId = 2
        val expectationLoading = "Loading"
        val expectationResult = "error"

        presenter.observeComponentState(presenterKey, mockLifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                errorId -> assertTrue(
                    it.getModelView().get().contains(
                        "class java.lang.String cannot be cast to class"
                    )
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            key = presenterKey,
            source = Observable.just(expectationResult),
            success = { null },
            loading = TestChildViewState(loadingId, TestModelView(expectationLoading)),
            error = {
                val message = it.message ?: "Error message are null"
                TestChildViewState(errorId, TestModelView(message))
            }
        )
        presenter.detachView(presenterKey)
    }

    @Test
    fun `test bindViewState when success but key not valid`() {
        configureLifecycleProvider()

        val loadingId = 1
        val errorId = 2
        val expectationLoading = "Loading"
        val expectationResult = "error"

        presenter.observeComponentState(presenterKey, mockLifecycleProvider) {
            when (it.getId()) {
                loadingId -> assertEquals(
                    expectationLoading, it.getModelView().get()
                )

                errorId -> assertTrue(
                    it.getModelView().get().contains(
                        "class java.lang.String cannot be cast to class"
                    )
                )

                else -> { /* Handle other ViewStates */
                }
            }
        }

        presenter.testBindViewState(
            key = TEST_NOT_FOUND_COMPONENT_KEY,
            source = Observable.just(expectationResult),
            success = { null },
            loading = TestChildViewState(loadingId, TestModelView(expectationLoading)),
            error = {
                val message = it.message ?: "Error message are null"
                TestChildViewState(errorId, TestModelView(message))
            }
        )
    }

    @Test
    fun `test bindViewState when error return null value`() {
        configureLifecycleProvider()

        val loadingId = 1
        val successId = 2
        val expectationLoading = "Loading"
        val expectationResult = "success"

        presenter.observeComponentState(presenterKey, mockLifecycleProvider) {
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
            key = presenterKey,
            source = Observable.just(expectationResult),
            success = { TestChildViewState(successId, TestModelView(it)) },
            loading = TestChildViewState(loadingId, TestModelView(expectationLoading)),
            error = { null }
        )

        presenter.detachView(TEST_NOT_FOUND_COMPONENT_KEY)
        presenter.detachView(presenterKey)
    }

    @Test
    fun `test bindViewState when error but error is null`() {
        runFullObserve()
        presenter.testableDestroy()
        assertFalse(presenter.validatePaused<Any>(TEST_NOT_FOUND_COMPONENT_KEY).call(null))

        val result = presenter.getComponentState(TEST_NOT_FOUND_COMPONENT_KEY)
        assertNull(result)
    }

    @Test
    fun `test bindViewState when key empty`() {
        runFullObserve()
        presenter.observeComponentState(presenterKey, mockLifecycleProvider) { }
    }

    @Test
    fun `test validatePaused, attachView when empty And detachView when not found`() {
        presenter.attachView(presenterKey)
        assertTrue(presenter.validatePaused<Any>(presenterKey).call(null))
        presenter.detachView(TEST_NOT_FOUND_COMPONENT_KEY)

        presenter.attachView("")
    }

    @Test
    fun `test getComponentState when valid`() {
        val result = presenter.getComponentState(presenterKey) as TestChildViewState
        assertNotNull(result)
        presenter.emitComponentState(presenterKey, result)
    }

    @Test
    fun `test observeViewState when lifecycle is ON_CREATE`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_CREATE))
    }

    @Test
    fun `test observeViewState when lifecycle is ON_DESTROY`() {
        runFullObserve()
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_DESTROY))
    }

    @Test
    fun `test observeViewState when lifecycle is Another`() {
        presenter.observeComponentState(presenterKey, mockLifecycleProvider) { }
        lifecycleEventSubject.onNext(RxLifecycleEvent(mockLifecycleOwner, Event.ON_ANY))
    }

    private fun runFullObserve() {
        configureLifecycleProvider()

        val successId = 2
        val errorId = 3
        val expectationResult = "success"
        val expectationError = "Unknown error"

        presenter.observeViewState(mockLifecycleProvider) { }

        presenter.observeComponentState(presenterKey, mockLifecycleProvider) {
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
            key = presenterKey,
            source = Observable.error(Throwable(expectationError)),
            success = { TestChildViewState(successId, TestModelView(expectationResult)) },
        )
    }

}