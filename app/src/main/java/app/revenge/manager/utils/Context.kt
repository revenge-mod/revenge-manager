package app.revenge.manager.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AnyRes
import java.io.InputStream

/**
 * Get the raw bytes for a resource.
 * @param id The resource identifier
 * @return The resource's raw bytes as stored inside the APK
 */
fun Context.getResBytes(@AnyRes id: Int): ByteArray {
    val tValue = TypedValue()
    this.resources.getValue(
        /* id = */ id,
        /* outValue = */ tValue,
        /* resolveRefs = */ true,
    )

    val resPath = tValue.string.toString()

    return this.javaClass.classLoader
        ?.getResourceAsStream(resPath)
        ?.use(InputStream::readBytes)
        ?: error("Failed to get resource file $resPath from APK")
}
