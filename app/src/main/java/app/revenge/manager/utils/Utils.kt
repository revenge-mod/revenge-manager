package app.revenge.manager.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.collection.ObjectList
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import app.revenge.manager.BuildConfig
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

val mainThread = Handler(Looper.getMainLooper())

fun mainThread(block: () -> Unit) {
    mainThread.post(block)
}

fun Context.copyText(text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    clipboardManager.setPrimaryClip(ClipData.newPlainText(app.revenge.manager.BuildConfig.APPLICATION_ID, text))
}

fun Context.showToast(@StringRes res: Int, vararg params: Any, short: Boolean = true) {
    Toast.makeText(
        this,
        getString(res, *params),
        if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}

/**
 * Remembers an activity result launcher used to save files to a user-specified location
 */
@Composable
fun rememberFileSaveLauncher(content: String, mimeType: String = "text/plain"): ManagedActivityResultLauncher<String, Uri?> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument(mimeType)) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(uri).use { stream ->
                stream?.write(content.toByteArray())
            }
        }
    }
}

private val cachedBitmaps: MutableMap<Int, MutableMap<Int, Bitmap>> = mutableMapOf()

context(Context)
private val Int.dp: Int
    get() = (45 * this@Context.resources.displayMetrics.density + 0.5f).toInt()

fun Context.getBitmap(@DrawableRes icon: Int, size: Int): Bitmap {
    cachedBitmaps[icon]?.let { it[size]?.let { bitmap -> return bitmap } }
    val sizePx = size.dp

    val bitmap = AppCompatResources.getDrawable(this, icon)!!.toBitmap(
        height = sizePx,
        width = sizePx
    )

    cachedBitmaps[icon] = mutableMapOf()
    cachedBitmaps[icon]?.let {
        it[size] = bitmap
    }

    return bitmap
}

// Thanks to https://gist.github.com/Muyangmin/e8ec1002c930d8df3df46b306d03315d
fun getSystemProp(prop: String): String? {
    val line: String
    var input = null as BufferedReader?
    try {
        val proc = Runtime.getRuntime().exec("getprop $prop")
        input = BufferedReader(InputStreamReader(proc.inputStream), 1024)
        line = input.readLine()
        input.close()
    } catch (e: IOException) {
        return null
    } finally {
        input?.let {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return line
}

val isMiui: Boolean
    get() = getSystemProp("ro.miui.ui.version.name")?.isNotEmpty() ?: false


inline fun <E> ObjectList<E>.find(block: (E) -> Boolean): E? {
    forEach { value ->
        if (block(value))
            return value
    }

    return null
}