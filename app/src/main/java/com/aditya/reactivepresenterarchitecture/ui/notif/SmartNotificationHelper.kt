package com.aditya.reactivepresenterarchitecture.ui.notif

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.aditya.reactivepresenterarchitecture.R
import java.util.LinkedList
import java.util.Queue

class SmartNotificationHelper(context: Context) {
    private val context: Context = context.applicationContext
    private val notificationManager: NotificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager

    // Menggunakan Queue untuk melacak notifikasi aktif (yang pertama masuk, yang pertama keluar)
    private val activeNotificationIds: Queue<Int?> = LinkedList<Int?>()
    var totalMessageCount = 0

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Smart Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Method utama untuk menangani dan menampilkan notifikasi pesan baru.
     * @param conversationId ID unik untuk setiap percakapan/chat.
     * @param senderName Nama pengirim.
     * @param message Teks pesan.
     */
    fun showMessage(conversationId: Int, senderName: String?, message: String?) {
        totalMessageCount++

        // LANGKAH 1: Hapus notifikasi paling lama jika sudah mencapai batas
        if (activeNotificationIds.size >= MAX_VISIBLE_NOTIFICATIONS) {
            val oldestId = activeNotificationIds.poll() // Ambil dan hapus ID tertua dari antrian
            if (oldestId != null) {
                Log.d(
                    "NotificationHelper",
                    "Limit reached. Canceling oldest notification: ID " + oldestId
                )
                notificationManager.cancel(oldestId)
            }
        }

        // LANGKAH 2: Buat dan tampilkan notifikasi individual yang baru
        val sender = Person.Builder().setName(senderName).build()
        val style = NotificationCompat.MessagingStyle(sender)
            .addMessage(message, System.currentTimeMillis(), sender)

        val individualNotification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(style)
            .setGroup(GROUP_KEY)
            .build()

        notificationManager.notify(conversationId, individualNotification)

        // Tambahkan ID baru ke dalam antrian
        activeNotificationIds.add(conversationId)

        // LANGKAH 3: Buat dan update notifikasi ringkasan (summary)
        updateSummaryNotification()
    }

    private fun updateSummaryNotification() {
        val activeCount = activeNotificationIds.size
        val summaryContentText: String?

        // Tentukan teks konten berdasarkan jumlah total pesan
        if (totalMessageCount > MAX_VISIBLE_NOTIFICATIONS) {
            val hiddenMessageCount = totalMessageCount - activeCount
            summaryContentText = context.getResources().getQuantityString(
                R.plurals.summary_message_and_more,
                hiddenMessageCount,
                hiddenMessageCount
            )
        } else {
            summaryContentText = "$totalMessageCount new messages"
        }

        val summaryNotification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("New Messages")
            .setContentText(summaryContentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()

        notificationManager.notify(SUMMARY_ID, summaryNotification)
    }

    companion object {
        private const val CHANNEL_ID = "smart_chat_channel"
        private const val GROUP_KEY = "com.example.myapp.CHAT_GROUP"
        private const val SUMMARY_ID = 0
        private const val MAX_VISIBLE_NOTIFICATIONS = 20
    }
}