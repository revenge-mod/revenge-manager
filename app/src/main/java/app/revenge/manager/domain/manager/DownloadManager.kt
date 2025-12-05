package app.revenge.manager.domain.manager

import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException

class DownloadManager(
    private val httpClient: HttpClient
) {

    suspend fun downloadUpdate(url: String, out: File, onProgressUpdate: (Float?) -> Unit) {
        download(url, out) { progress ->
            onProgressUpdate(progress)
        }
    }

    /**
     * Starts a cancellable download using Ktor.
     * If the current [CoroutineScope] is cancelled, then the download will be cancelled.
     * @param url Remote src url.
     * @param out Target path to download to.
     * @param onProgressUpdate Callback for progress updates in a `[0,1]` range. `null` is emitted
     *                         once at the start to indicate a pending state.
     */
    suspend fun download(
        url: String,
        out: File,
        onProgressUpdate: (Float?) -> Unit
    ): DownloadResult {
        onProgressUpdate(null)
        out.parentFile?.mkdirs()

        val tempOut = out.resolveSibling("${out.name}.tmp")
        if (tempOut.exists()) tempOut.delete()

        var bytesCopied = 0L
        var totalBytes = 0L

        try {
            httpClient.prepareGet(url).execute { response ->
                if (!response.status.isSuccess()) {
                    throw IOException("HTTP Error: ${response.status}")
                }

                val body = response.bodyAsChannel()
                totalBytes = response.contentLength() ?: 0L

                tempOut.outputStream().use { output ->
                    val buffer = ByteArray(1024 * 256)
                    var bytesRead: Int

                    while (!body.isClosedForRead) {
                        bytesRead = body.readAvailable(buffer)
                        if (bytesRead < 0) break
                        output.write(buffer, 0, bytesRead)
                        bytesCopied += bytesRead

                        if (totalBytes > 0) {
                            val progress =
                                (bytesCopied.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                            onProgressUpdate(progress)
                        } else {
                            onProgressUpdate(null)
                        }
                    }
                }
            }
        } catch (_: CancellationException) {
            tempOut.delete()
            return DownloadResult.Cancelled
        } catch (_: SocketTimeoutException) {
            tempOut.delete()
            return DownloadResult.Error("Download timed out")
        } catch (e: IOException) {
            tempOut.delete()
            return DownloadResult.Error("Download failed: ${e.message}")
        } catch (e: Exception) {
            tempOut.delete()
            return DownloadResult.Error(e.message ?: "Unknown download error")
        }

        if (totalBytes > 0 && bytesCopied < totalBytes) {
            val difference = totalBytes - bytesCopied
            val percentageDifference = difference.toFloat() / totalBytes.toFloat()

            // We consider a download incomplete if more than 1% of the data is missing, because
            // sometimes the content length provided by the server can be slightly off
            if (percentageDifference > 0.01f) {
                tempOut.delete()
                return DownloadResult.Error("Download incomplete. Expected $totalBytes bytes, but got $bytesCopied")
            }
        }

        if (tempOut.exists() && tempOut.length() > 0) {
            if (out.exists()) out.delete()
            tempOut.renameTo(out)
            return DownloadResult.Success
        } else {
            tempOut.delete()
            return DownloadResult.Error("Downloaded file is empty or missing")
        }
    }
}

sealed interface DownloadResult {
    data object Success : DownloadResult
    data object Cancelled : DownloadResult
    data class Error(val debugReason: String) : DownloadResult
}
