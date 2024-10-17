package app.revenge.manager.installer.shizuku

import android.content.pm.PackageManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener

@OptIn(DelicateCoroutinesApi::class)
object ShizukuPermissions {

    private const val REQUEST_CODE = 1

    private val _permissionsGranted = MutableSharedFlow<Boolean>(replay = 0)
    private lateinit var permissionResultListener: OnRequestPermissionResultListener

    fun requestShizukuPermissions() {
        if (!Shizuku.pingBinder()) {
            GlobalScope.launch { _permissionsGranted.emit(false) }
            return
        }
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            GlobalScope.launch { _permissionsGranted.emit(true) }
            return
        }

        Shizuku.addRequestPermissionResultListener(permissionResultListener)
        Shizuku.requestPermission(REQUEST_CODE)
    }

    suspend fun waitShizukuPermissions(): Boolean {
        requestShizukuPermissions()
        return _permissionsGranted.first()
    }

    init {
        permissionResultListener = OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode != REQUEST_CODE) return@OnRequestPermissionResultListener

            Shizuku.removeRequestPermissionResultListener(permissionResultListener)

            GlobalScope.launch {
                _permissionsGranted.emit(grantResult == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

}
