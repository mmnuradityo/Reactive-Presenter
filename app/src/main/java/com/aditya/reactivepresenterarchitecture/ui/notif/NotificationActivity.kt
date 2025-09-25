package com.aditya.reactivepresenterarchitecture.ui.notif

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aditya.reactivepresenterarchitecture.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationHelper: SmartNotificationHelper
    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onWindow()
        initComponent()
        listener()
    }

    private fun onWindow() {
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initComponent() {
        notificationHelper = SmartNotificationHelper(this);
    }

    private fun listener() {
        binding.btnNotif.setOnClickListener {
            val conversationId = (System.currentTimeMillis() % 10000).toInt()
            val sender = "User " + conversationId
            val message = "This is message number " + (notificationHelper.totalMessageCount + 1)
            notificationHelper.showMessage(conversationId, sender, message)
        }
    }

}