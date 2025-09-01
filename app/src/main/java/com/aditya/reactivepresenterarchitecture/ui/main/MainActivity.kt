package com.aditya.reactivepresenterarchitecture.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aditya.reactivepresenterarchitecture.R
import com.aditya.reactivepresenterarchitecture.databinding.ActivityMainBinding
import com.aditya.reactivepresenterarchitecture.ui.PresenterFactory

class MainActivity : AppCompatActivity() {

    private val presenter: MainPresenter = PresenterFactory.create(lifecycle)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onWindow()
        initView()
    }

    private fun onWindow() {
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        presenter.observeViewState {
            when (it) {
                is MainViewState.Empty -> {
                    binding.tvText.text = "Greetings"
                }

                is MainViewState.Loading -> {
                    binding.tvText.text =
                        if (it.getModelView().list.isEmpty()) "Loading...." else it.getModelView().list[3]
                }

                is MainViewState.StringList -> {
                    binding.tvText.text = it.model.list[3]
                }

                is MainViewState.Error -> {
                    binding.tvText.text = it.model.error
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getList()
    }

}