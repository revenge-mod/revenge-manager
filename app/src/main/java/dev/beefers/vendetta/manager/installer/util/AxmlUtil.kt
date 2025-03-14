// https://github.com/Aliucord/Manager/blob/main/app/src/main/kotlin/com/aliucord/manager/installer/util/AxmlUtil.kt
package dev.beefers.vendetta.manager.installer.util

import dev.beefers.vendetta.manager.utils.find
import com.github.diamondminer88.zip.ZipReader
import com.github.diamondminer88.zip.ZipWriter
import com.google.devrel.gmscore.tools.apk.arsc.*
import java.io.File

object AxmlUtil {
    /**
     * Read and parse a specific axml resource inside an APK
     * @param apk The source apk
     * @param resourcePath The full path to the axml file inside the apk, which may be flattened.
     */
    private fun readAxml(apk: File, resourcePath: String): BinaryResourceFile {
        val bytes = ZipReader(apk).use { it.openEntry(resourcePath)?.read() }
            ?: error("APK missing resource file at $resourcePath")

        return try {
            BinaryResourceFile(bytes)
        } catch (t: Throwable) {
            throw Error("Failed to parse axml at $resourcePath", t)
        }
    }

    /**
     * Get the only top-level chunk in an axml file.
     */
    private fun BinaryResourceFile.getMainAxmlChunk(): XmlChunk {
        if (this.chunks.size > 1)
            error("More than 1 top level chunk in axml")

        return this.chunks.first() as? XmlChunk
            ?: error("Invalid top-level axml chunk")
    }

    /**
     * Finds the first chunk with a matching [name] in a flattened chunk list.
     * @receiver The top level XmlChunk ([getMainAxmlChunk])
     */
    private fun XmlChunk.getStartElementChunk(name: String): XmlStartElementChunk? {
        val nameIdx = this.stringPool.indexOf(name)

        return this.chunks
            .find { it is XmlStartElementChunk && it.nameIndex == nameIdx }
            as? XmlStartElementChunk
    }

    /**
     * Finds the first attribute with a matching name (ignoring namespace)
     * in a starting element chunk.
     */
    private fun XmlStartElementChunk.getAttribute(name: String): XmlAttribute {
        val nameIdx = (this.parent as XmlChunk).stringPool.indexOf(name)

        return this.attributes
            .find { it.nameIndex() == nameIdx }
            ?: error("Failed to find $name attribute in an axml chunk")
    }

    /**
     * Patches an <adaptive-icon> axml file to change the `background`, `foreground`, and `monochrome` resource references.
     * If any of the following are not null, then they will be patched.
     * @param backgroundColor A color resource id to replace <background> with.
     *
     * @param foregroundIcon A drawable resource id to replace <foreground> with.
     * @param monochromeIcon A drawable resource id to add or replace <monochrome> with.
     */
    fun patchAdaptiveIcon(
        apk: File,
        resourcePath: String,
        referencePath: String,
        backgroundColor: BinaryResourceIdentifier? = null,
        foregroundIcon: BinaryResourceIdentifier? = null,
        monochromeIcon: BinaryResourceIdentifier? = null,
    ) {
        val xml = readAxml(apk, referencePath)
        val xmlChunk = xml.getMainAxmlChunk()

        // Patch the background color resource reference
        if (backgroundColor != null) {
            val chunk = xmlChunk.getStartElementChunk("background")!!
            val attribute = chunk.getAttribute("drawable")
            attribute.typedValue().setValue(
                /* type = */ BinaryResourceValue.Type.REFERENCE,
                /* data = */ backgroundColor.resourceId(),
            )
        }

        // Patch the foreground drawable reference
        if (foregroundIcon != null) {
            val chunk = xmlChunk.getStartElementChunk("foreground")!!
            val attribute = chunk.getAttribute("drawable")
            attribute.typedValue().setValue(
                /* type = */ BinaryResourceValue.Type.REFERENCE,
                /* data = */ foregroundIcon.resourceId(),
            )
        }

        // Add or replace the monochrome drawable reference
        if (monochromeIcon != null) {
            // <monochrome> already exists, patch existing chunk
            val existingChunk = xmlChunk.getStartElementChunk("monochrome")
            if (existingChunk != null) {
                val attribute = existingChunk.getAttribute("drawable")
                attribute.typedValue().setValue(
                    /* type = */ BinaryResourceValue.Type.REFERENCE,
                    /* data = */ monochromeIcon.resourceId(),
                )
            }
            // Add a new start & end chunk since they don't exist
            // `<monochrome android:drawable="@drawable/xyz"></monochrome>
            else {
                val iconEndChunkIdx = xmlChunk.chunks
                    .indexOfLast { it is XmlEndElementChunk && it.name == "adaptive-icon" }

                val namespaceIdx = xmlChunk.stringPool.indexOf("http://schemas.android.com/apk/res/android")
                val drawableIdx = xmlChunk.stringPool.indexOf("drawable")
                val monochromeIdx = xmlChunk.stringPool.addString("monochrome")

                val startChunk = XmlStartElementChunk(
                    /* namespaceIndex = */ -1,
                    /* nameIndex = */ monochromeIdx,
                    /* idIndex = */ -1,
                    /* classIndex = */ -1,
                    /* styleIndex = */ -1,
                    /* attributes = */
                    listOf(
                        XmlAttribute(
                            /* namespaceIndex = */ namespaceIdx,
                            /* nameIndex = */ drawableIdx,
                            /* rawValueIndex = */ -1,
                            /* typedValue = */
                            BinaryResourceValue(
                                /* type = */ BinaryResourceValue.Type.REFERENCE,
                                /* data = */ monochromeIcon.resourceId(),
                            ),
                            // This is wrong but it doesn't matter here as long as this attribute isn't stringified
                            /* parent = */ null,
                        )
                    ),
                    /* parent = */ xmlChunk,
                )
                val endChunk = XmlEndElementChunk(
                    /* namespaceIndex = */ namespaceIdx,
                    /* nameIndex = */ monochromeIdx,
                    /* parent = */ xmlChunk,
                )

                xmlChunk.addChunk(iconEndChunkIdx, startChunk)
                xmlChunk.addChunk(iconEndChunkIdx + 1, endChunk)
            }
        }

        ZipWriter(apk, /* append = */ true).use { zip ->
            zip.deleteEntry(resourcePath)
            zip.writeEntry(resourcePath, xml.toByteArray())
        }
    }

    /**
     * From an APK, read the manifest's `icon` and `roundIcon` references to a resource.
     * This is then used to get the filename of the resource from `resources.arsc`.
     */
    fun readManifestIconInfo(apk: File): ManifestIconInfo {
        val manifestBytes = ZipReader(apk).use { it.openEntry("AndroidManifest.xml")?.read() }
            ?: error("APK missing manifest")
        val manifest = BinaryResourceFile(manifestBytes)
        val mainChunk = manifest.getMainAxmlChunk()

        // Prefetch string indexes to avoid parsing the entire string pool
        val iconStringIdx = mainChunk.stringPool.indexOf("icon")
        val roundIconStringIdx = mainChunk.stringPool.indexOf("roundIcon")
        val applicationStringIdx = mainChunk.stringPool.indexOf("application")

        val applicationChunk = mainChunk.chunks
            .find { it is XmlStartElementChunk && it.nameIndex == applicationStringIdx } as? XmlStartElementChunk
            ?: error("Unable to find <application> in manifest")

        val squareIcon = applicationChunk.attributes
            .find { it.nameIndex() == iconStringIdx }
            ?: error("Unable to find android:icon in manifest")

        val roundIcon = applicationChunk.attributes
            .find { it.nameIndex() == roundIconStringIdx }
            ?: error("Unable to find android:roundIcon in manifest")

        assert(squareIcon.typedValue().type() == BinaryResourceValue.Type.REFERENCE)
        assert(roundIcon.typedValue().type() == BinaryResourceValue.Type.REFERENCE)

        return ManifestIconInfo(
            // Resource IDs into resources.arsc
            squareIcon = BinaryResourceIdentifier.create(squareIcon.typedValue().data()),
            roundIcon = BinaryResourceIdentifier.create(roundIcon.typedValue().data()),
        )
    }

    data class ManifestIconInfo(
        val squareIcon: BinaryResourceIdentifier,
        val roundIcon: BinaryResourceIdentifier,
    )
}
