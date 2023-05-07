package no.iktdev.notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

abstract class Notification(context: Context): Notifications(context) {

    @Suppress("CanBePrivate")
    val notification: NotificationCompat.Builder by lazy { create() }

    open fun getBaseIntent(): Intent {
        Log.w(this::class.java.simpleName, "getBaseIntent is not overridden, returning blank intent")
        return Intent()
    }

    abstract var notificationId: Int
    protected val channelId: String by lazy { createChannelId() }

    open fun createChannelId(): String {
        throw RuntimeException("Please define a channel id for ${this::class.java.simpleName}")
    }

    abstract fun create(): NotificationCompat.Builder

    open fun publish() {
        onPublish(notificationId, notification.build())
    }

    open fun cancel() {
        onCancel(notificationId)
    }

    fun startForeground(service: Service) {
        service.startForeground(notificationId, notification.build())
    }
}