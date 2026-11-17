package app.revenge.manager.updatechecker.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.revenge.manager.updatechecker.UpdateNotifications
import app.revenge.manager.utils.Intents

class UpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val versionCode = intent.getStringExtra(Intents.Extras.VERSION) ?: return
        UpdateNotifications.showUpdateAvailable(context, versionCode)
    }

}
