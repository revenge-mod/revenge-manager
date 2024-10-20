package app.revenge.manager.installer.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lsposed.patch.LSPatch
import org.lsposed.patch.util.Logger
import java.io.File

object Patcher {

    suspend fun patch(
        logger: Logger,
        outputDir: File,
        apkPaths: List<String>,
        embeddedModules: List<String>
    ) {
        withContext(Dispatchers.IO) {
            LSPatch(
                logger,
                *apkPaths.toTypedArray(),
                "-o",
                outputDir.absolutePath,
                "-l",
                "0",
                "-v",
                "-m",
                *embeddedModules.toTypedArray(),
                "-k",
                app.revenge.manager.installer.util.Signer.keyStore.absolutePath,
                "password",
                "alias",
                "password"
            ).doCommandLine()
        }
    }

}