package app.revenge.manager.installer.session

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.SessionParams
import android.content.pm.PackageManager
import android.os.Build
import app.revenge.manager.installer.Installer
import java.io.File

internal class SessionInstaller(private val context: Context) : Installer {

    private val packageManager: PackageManager = context.packageManager

    override suspend fun installApks(silent: Boolean, vararg apks: File) {
        val params = SessionParams(SessionParams.MODE_FULL_INSTALL).apply {
            if (Build.VERSION.SDK_INT >= 31) {
                setInstallScenario(PackageManager.INSTALL_SCENARIO_FAST)

                if (silent) {
                    setRequireUserAction(SessionParams.USER_ACTION_NOT_REQUIRED)
                }
            }
        }

        val packageInstaller = packageManager.packageInstaller
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        apks.forEach { apk ->
            session.openWrite(apk.name, 0, apk.length()).use {
                it.write(apk.readBytes())
                session.fsync(it)
            }
        }

        val callbackIntent = Intent(context, InstallService::class.java).apply {
            action = "revenge.actions.ACTION_INSTALL"
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        val contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(context, 0, callbackIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getService(context, 0, callbackIntent, 0)
        }

        session.commit(contentIntent.intentSender)
        session.close()
    }
}