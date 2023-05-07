package no.iktdev.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

abstract class Channel(
        @Suppress("CanBePrivate") val context: Context
    ) {

    /**
     * Creates Channels from channels()
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels() {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(channels.values.toList().filterNotNull())
    }

    abstract val channels: Map<String, NotificationChannel?>
}