package com.aditya.reactivepresenterarchitecture.ui.mainfragment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.aditya.reactivepresenterarchitecture.databinding.ActivityMainFragmentBinding
import com.aditya.reactivepresenterarchitecture.manager.PresenterInjector
import com.aditya.reactivepresenterarchitecture.manager.PresenterProvider
import com.aditya.reactivepresenterarchitecture.ui.PresenterFactory
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.AFragment
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.BFragment

const val FRAGMENT_KEY = "FRAGMENT_KEY"

class MainFragmentActivity : AppCompatActivity(), PresenterInjector<MainFragmentPresenter> {

    private var currentFragment: Fragment? = AFragment.newInstance()
    private lateinit var binding: ActivityMainFragmentBinding
    private val presenter: MainFragmentPresenter = PresenterFactory.create(lifecycle)

    override fun obtainPresenter(presenterClass: Class<*>): MainFragmentPresenter {
        return PresenterProvider.obtain(
            presenter, this, presenterClass
        ) as MainFragmentPresenter
    }

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
        presenter.observeViewState {
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
        initFragment(currentFragment?.javaClass ?: AFragment::class.java)
    }

    private fun listener() {
        binding.btnOpenAFragment.setOnClickListener {
            initFragment(AFragment::class.java)
        }
        binding.btnOpenBFragment.setOnClickListener {
            initFragment(BFragment::class.java)
        }
    }

    private fun initFragment(aClassFragment: Class<*>) {
        val fragment = getOrCreateFragment(aClassFragment)
        supportFragmentManager.beginTransaction()
            .replace(binding.containerFragment.id, fragment, fragment.javaClass.simpleName)
            .commit()
        currentFragment = fragment
    }

    private fun getOrCreateFragment(aClassFragment: Class<*>): Fragment {
        var fragment: Fragment? = null
        supportFragmentManager.fragments.forEach {
            if (it.javaClass == aClassFragment) {
                fragment = it
            }
        }
        if (fragment == null) {
            when (aClassFragment) {
                AFragment::class.java -> fragment = AFragment.newInstance()
                BFragment::class.java -> fragment = BFragment.newInstance()
            }
        }
        return fragment!!
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