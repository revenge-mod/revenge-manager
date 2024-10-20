package app.revenge.manager.installer.util

import android.util.Log
import androidx.compose.runtime.Stable
import org.lsposed.patch.util.Logger

/**
 * Used to log events done during the patching process
 *
 * @param tag The tag to use for logcat
 */
@Stable
class Logger(
    private val tag: String
): Logger() {

    /**
     * All logs made with this [Logger]
     */
    val logs = mutableListOf<LogEntry>()

    /**
     * Prints a debug message to logcat and stores it in [logs]
     */
    override fun d(msg: String?) {
        log(msg, LogEntry.Level.DEBUG)
        Log.d(tag, msg.toString())
    }

    /**
     * Prints an info message to logcat and stores it in [logs]
     */
    override fun i(msg: String?) {
        log(msg, LogEntry.Level.INFO)
        Log.i(tag, msg.toString())
    }

    /**
     * Prints an error message to logcat and stores it in [logs]
     */
    override fun e(msg: String?) {
        log(msg, LogEntry.Level.ERROR)
        Log.e(tag, msg.toString())
    }

    /**
     * Prints an error message and stacktrace with a preceding empty line.
     */
    fun e(msg: String?, th: Throwable?) {
        newline()
        e(msg)
        if (th != null) e(th.stackTraceToString().trim())
    }

    /**
     * Stores a log entry
     */
    private fun log(msg: String?, level: LogEntry.Level) {
        msg?.let {
            msg.split("\n").forEach {
                logs += LogEntry(it, level)
            }
        }
    }

    /**
     * Prints an empty line
     */
    private fun newline() {
        i("\n")
    }

}