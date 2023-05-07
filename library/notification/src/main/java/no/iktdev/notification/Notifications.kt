package no.iktdev.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

abstract class Notifications(private val context: Context) {
    companion object {
        private val publishedIds: MutableList<Int> = mutableListOf()
        fun isAllowedToSendNotifications(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
    }
    private val manager = NotificationManagerCompat.from(context)

    /**
     * @return Copy of published ids
     */
    fun publishingIdsUsed(): List<Int> = publishedIds.toList()

    fun onPublish(id: Int, builder: NotificationCompat.Builder) {
        this.onPublish(id, builder.build())
    }

    @SuppressLint("MissingPermission") // Permission is checked!
    fun onPublish(id: Int, notification: android.app.Notification) {
        if (isAllowedToSendNotifications(context)) {
            manager.notify(id, notification)
            publishedIds.add(id)
        }
    }

    /**
     * Cancels notification on @param id: Int
     */
    fun onCancel(id: Int) {
        manager.cancel(id)
        publishedIds.removeAll { it == id}
    }

    /**
     * Sets content intent for notification builder
     */
    fun addTapToLaunch(builder: NotificationCompat.Builder, activity: Class<*>, action: String? = null, bundle: Bundle? = null) {
        val intent = Intent(context, activity)
        action?.let { intent.action = action }
        bundle?.let { intent.putExtras(it) }
        val pendingIntentFinalFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFinalFlag)
        builder.setContentIntent(pendingIntent)
    }

    /*fun addIconButton(builder: NotificationCompat.Builder, bundle: Bundle,
                      @DrawableRes icon: Int,
                      @StringRes text: Int) {
        NotificationCompat.Action.Builder()
    }

    fun addIconButton(builder: NotificationCompat.Builder, bundle: Bundle,
                      @DrawableRes icon: Int,
                      text: String
    ) {

    }*/

    protected open fun getNotificationStackIntent(
        parentActivity: Class<*>?,
        targetActivity: Class<*>?
    ): PendingIntent? {
        val parent = Intent(context, parentActivity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val target = Intent(context, targetActivity).setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        return PendingIntent.getActivities(
            context,
            0,
            arrayOf(parent, target),
            getPendingIntentFlag(PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }

    fun isLegacyDevice(): Boolean { return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P }
    fun isLegacyNightMode(): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    protected open fun getPendingIntentFlag(flags: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) flags or PendingIntent.FLAG_IMMUTABLE else flags
    }
    protected open fun getPendingIntent(
        requestCode: Int,
        intent: Intent,
        flag: Int
    ): PendingIntent? {
        return PendingIntent.getBroadcast(context, requestCode, intent, getPendingIntentFlag(flag))
    }
}