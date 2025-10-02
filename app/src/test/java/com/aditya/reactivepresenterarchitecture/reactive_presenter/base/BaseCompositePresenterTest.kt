package com.aditya.reactivepresenterarchitecture.reactive_presenter.base;

import com.aditya.reactivepresenterarchitecture.reactive_presenter.component_test.TestChildViewState
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class BaseCompositePresenterTest<P>(val presenterKey: String): BaseReactivePresenterTest<P>()
        where P : ICompositeReactivePresenter<TestChildViewState> {

    @BeforeEach
    override fun setUp() {
        super.setUp()
        presenter.attachView(presenterKey)
    }

    @AfterEach
    override fun tearDown() {
        presenter.detachView("")
        super.tearDown()
    }

}
