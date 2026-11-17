package app.revenge.manager.updatechecker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import app.revenge.manager.R
import app.revenge.manager.ui.activity.MainActivity
import app.revenge.manager.utils.Channels
import app.revenge.manager.utils.DiscordVersion
import app.revenge.manager.utils.Intents

object UpdateNotifications {
    private const val INSTALL_RESULT_NOTIFICATION_ID = 10001

    fun showUpdateAvailable(context: Context, versionCode: String) {
        val version = DiscordVersion.fromVersionCode(versionCode) ?: return
        val notificationId = versionCode.toIntOrNull() ?: versionCode.hashCode()
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            action = Intents.Actions.INSTALL
            putExtra(Intents.Extras.VERSION, versionCode)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val clickIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notify(context, notificationId) {
            setContentTitle(context.getString(R.string.title_update_available))
            setContentText(context.getString(R.string.action_tap_to_install, version.toString()))
            setContentIntent(clickIntent)
            setAutoCancel(true)
        }
    }

    fun showInstalled(context: Context) {
        notify(context, INSTALL_RESULT_NOTIFICATION_ID) {
            setContentTitle(context.getString(R.string.title_update_installed))
            setContentText(context.getString(R.string.msg_update_installed))
            setAutoCancel(true)
        }
    }

    fun showApprovalRequired(context: Context, approvalIntent: PendingIntent) {
        notify(context, INSTALL_RESULT_NOTIFICATION_ID) {
            setContentTitle(context.getString(R.string.title_update_needs_approval))
            setContentText(context.getString(R.string.msg_update_needs_approval))
            setContentIntent(approvalIntent)
            setAutoCancel(true)
        }
    }

    fun showInstallFailed(context: Context, reason: String) {
        notify(context, INSTALL_RESULT_NOTIFICATION_ID) {
            setContentTitle(context.getString(R.string.title_update_failed))
            setContentText(context.getString(R.string.msg_update_failed, reason))
            setAutoCancel(true)
        }
    }

    private inline fun notify(
        context: Context,
        notificationId: Int,
        configure: NotificationCompat.Builder.() -> Unit
    ) {
        val notification = NotificationCompat.Builder(context, Channels.UPDATE)
            .setSmallIcon(R.drawable.ic_update)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply(configure)
            .build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(notificationId, notification)
    }
}
