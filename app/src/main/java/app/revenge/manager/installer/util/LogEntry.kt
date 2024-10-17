package app.revenge.manager.installer.util

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.datetime.Clock
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

@Stable
data class LogEntry(
    val message: String,
    val level: Level,
    private val timestampMillis: Long = Clock.System.now().toEpochMilliseconds()
): Parcelable, Serializable {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(message)
        dest.writeInt(level.ordinal)
        dest.writeLong(timestampMillis)
    }

    override fun toString(): String {
        return "${formatTimestamp()} [${level.name[0]}] $message"
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTimestamp(): String {
        return SimpleDateFormat("MM-dd-yyyy h:mm:ssa").format(Date(timestampMillis))
    }

    enum class Level {
        DEBUG,
        INFO,
        ERROR
    }

    companion object CREATOR: Parcelable.Creator<LogEntry?> {

        override fun createFromParcel(source: Parcel): LogEntry {
            val message = source.readString()!!
            val level = Level.entries[source.readInt()]
            val timestamp = source.readLong()
            return LogEntry(message, level, timestamp)
        }

        override fun newArray(size: Int): Array<LogEntry?> {
            return arrayOfNulls(size)
        }

    }

}