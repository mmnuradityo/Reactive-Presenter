package com.aditya.reactivepresenterarchitecture.ui.mainfragment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.aditya.reactivepresenterarchitecture.databinding.ActivityMainFragmentBinding
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.AFragment
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.BFragment

const val FRAGMENT_KEY = "FRAGMENT_KEY"

class MainFragmentActivity : AppCompatActivity() {

    private val presenter: MainFragmentPresenter = PresenterFactory.getOrCreate()
    private lateinit var binding: ActivityMainFragmentBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onWindow()
        initView()
        listener()
    }

    private fun onWindow() {
        enableEdgeToEdge()
        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        presenter.observeViewState(lifecycle) {
            when (it) {
                is MainFragmentViewState.Empty -> {
                    binding.tvText.text = "Greetings From Main"
                }

                is MainFragmentViewState.Loading -> {
                    binding.tvText.text = it.getModelView().result.ifEmpty { "Loading Main...." }
                }

                is MainFragmentViewState.Data -> {
                    binding.tvText.text = it.getModelView().result
                }

                is MainFragmentViewState.Error -> {
                    binding.tvText.text = it.getModelView().error
                }
            }
        }
        initFragment(currentFragment?.javaClass)
    }

    private fun listener() {
        binding.btnOpenAFragment.setOnClickListener {
            initFragment(AFragment::class.java)
        }
        binding.btnOpenBFragment.setOnClickListener {
            initFragment(BFragment::class.java)
        }
    }

    private fun initFragment(aClassFragment: Class<*>?) {
        var fragment: Fragment? = null

        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach {
            if (aClassFragment != null && it.javaClass == aClassFragment) {
                fragment = it
            } else if (currentFragment == null && it.isAdded && !it.isHidden) {
                fragment = it
            } else {
                transaction.hide(it)
            }
        }

        if (fragment == null) {
            fragment = when (aClassFragment) {
                BFragment::class.java -> BFragment.newInstance()
                else -> AFragment.newInstance()
            }
            transaction.add(
                binding.containerFragment.id, fragment!!, fragment!!.javaClass.simpleName
            )
        } else {
            transaction.show(fragment!!)
        }

        transaction.commit()
        currentFragment = fragment
    }

    override fun onResume() {
        super.onResume()
        presenter.getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentFragment = null

    }
}