package com.aditya.reactivepresenterarchitecture.reactive_presenter.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aditya.reactivepresenterarchitecture.reactive_presenter.base.IReactivePresenter
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.RxLifecycleProvider

abstract class BaseReactiveActivity<P>: AppCompatActivity(), IBaseActivityReactive<P> where P : IReactivePresenter {

    protected val presenter: P = this.onPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onWindow()
        initViews()
        observeState(RxLifecycleProvider(lifecycle))
        listener()
        loadData()
    }

    open fun onWindow() {
        enableEdgeToEdge()
        val view = this.onCreateView()
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}