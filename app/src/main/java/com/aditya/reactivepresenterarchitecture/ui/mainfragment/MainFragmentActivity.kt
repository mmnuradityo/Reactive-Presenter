package com.aditya.reactivepresenterarchitecture.ui.mainfragment

import android.view.View
import androidx.fragment.app.Fragment
import com.aditya.reactivepresenterarchitecture.databinding.ActivityMainFragmentBinding
import com.aditya.reactivepresenterarchitecture.reactive_presenter.PresenterFactory
import com.aditya.reactivepresenterarchitecture.reactive_presenter.lifecycle.IRxLifecycleProvider
import com.aditya.reactivepresenterarchitecture.reactive_presenter.ui.BaseReactiveActivity
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.AFragment
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.fragment.BFragment

const val FRAGMENT_KEY = "FRAGMENT_KEY"

class MainFragmentActivity : BaseReactiveActivity<MainFragmentPresenter>() {

    private lateinit var binding: ActivityMainFragmentBinding
    private var currentFragment: Fragment? = null

    override fun onPresenter(): MainFragmentPresenter = PresenterFactory.getOrCreate()

    override fun onCreateView(): View {
        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initViews() {
        initFragment(currentFragment?.javaClass)
    }

    override fun listener() {
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

    override fun observeState(lifecycleProvider: IRxLifecycleProvider) {
        presenter.observeViewState(lifecycleProvider) {
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