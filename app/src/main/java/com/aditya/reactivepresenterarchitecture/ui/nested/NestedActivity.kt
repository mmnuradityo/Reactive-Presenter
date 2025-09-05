package com.aditya.reactivepresenterarchitecture.ui.nested

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aditya.reactivepresenterarchitecture.databinding.ActivityNestedBinding
import com.aditya.reactivepresenterarchitecture.ui.nested.fragment.NestedParentFragment

const val NESTED_FRAGMENT_KEY = "nested_fragment_key"

class NestedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNestedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onWindow()
        initView()
    }

    private fun onWindow() {
        enableEdgeToEdge()
        binding = ActivityNestedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
    }

    private fun initView() {
        supportFragmentManager.beginTransaction().replace(
            binding.container.id,
            NestedParentFragment.newInstance(),
            NestedParentFragment::class.java.simpleName
        ).commit()
    }

}