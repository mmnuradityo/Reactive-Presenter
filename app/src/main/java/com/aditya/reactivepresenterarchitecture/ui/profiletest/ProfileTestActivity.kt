package com.aditya.reactivepresenterarchitecture.ui.profiletest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aditya.reactivepresenterarchitecture.R
import com.aditya.reactivepresenterarchitecture.databinding.ActivityProfileTestBinding
import com.aditya.reactivepresenterarchitecture.ui.PresenterFactory
import com.aditya.reactivepresenterarchitecture.ui.main.MainActivity
import com.aditya.reactivepresenterarchitecture.ui.main.MainPresenter
import com.aditya.reactivepresenterarchitecture.ui.mainfragment.MainFragmentActivity

class ProfileTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnOpenMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnOpenMainFragment.setOnClickListener {
            val intent = Intent(this, MainFragmentActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ProfileTestActivity", "onResume")
        val count = PresenterFactory.presentersCache.size
        Handler(Looper.getMainLooper()).postDelayed({
            if (count > 0) {
                Log.d("ProfileTestActivity", "onResume count: ${count}")
                Log.d(
                    "ProfileTestActivity",
                    "onResume key: ${PresenterFactory.presentersCache.keys}"
                )
                Log.d(
                    "ProfileTestActivity",
                    "onResume key: ${PresenterFactory.presentersCache[MainPresenter::class.java.simpleName] != null}"
                )
            }
        }, 2000);
    }
}