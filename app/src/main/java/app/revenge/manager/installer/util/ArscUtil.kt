// https://github.com/Aliucord/Manager/blob/main/app/src/main/kotlin/com/aliucord/manager/installer/util/ArscUtil.kt
package app.revenge.manager.installer.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.github.diamondminer88.zip.ZipReader
import com.google.devrel.gmscore.tools.apk.arsc.*
import app.revenge.manager.BuildConfig
import java.io.File

object ArscUtil {
    /**
     * Read and parse `resources.arsc` from an APK.
     */
    fun readArsc(apk: File): BinaryResourceFile {
        val bytes = ZipReader(apk).use { it.openEntry("resources.arsc")?.read() }
            ?: error("APK missing resources.arsc")

        return try {
            BinaryResourceFile(bytes)
        } catch (t: Throwable) {
            throw Error("Failed to parse resources.arsc", t)
        }
    }

    /**
     * Get the only top-level chunk in an arsc file.
     */
    fun BinaryResourceFile.getMainArscChunk(): ResourceTableChunk {
        if (this.chunks.size > 1)
            error("More than 1 top level chunk in resources.arsc")

        return this.chunks.first() as? ResourceTableChunk
            ?: error("Invalid top-level resources.arsc chunk")
    }

    /**
     * Get a singular package chunk in an arsc file.
     */
    fun BinaryResourceFile.getPackageChunk(): PackageChunk {
        return this.getMainArscChunk().packages.singleOrNull()
            ?: error("resources.arsc must contain exactly 1 package chunk")
    }

    /**
     * Adds a new color resource to all configuration variants in an arsc package.
     *
     * @param name The new resource name.
     * @param color The value of the new color resource.
     * @return The resource ID of the newly added resource.
     */
    fun PackageChunk.addColorResource(
        name: String,
        color: Color,
    ): BinaryResourceIdentifier {
        return this.addResource(
            typeName = "color",
            resourceName = name,
            configurations = { true },
            valueType = BinaryResourceValue.Type.INT_COLOR_ARGB8,
            valueData = color.toArgb(),
        )
    }

    /**
     * Adds a new color resource to the matching configuration variants in an arsc package.
     *
     * @param typeName The type of the resource (ex: `mipmap`, `drawable`, etc.)
     * @param resourceName The new resource name.
     * @param configurations A predicate whether to add the value into a matching type chunk.
     * @param valueType The type of the resource value.
     * @param valueData The raw data of the resource value.
     * @return The resource ID of the newly added resource.
     */
    fun PackageChunk.addResource(
        typeName: String,
        resourceName: String,
        configurations: (BinaryResourceConfiguration) -> Boolean,
        valueType: BinaryResourceValue.Type,
        valueData: Int,
    ): BinaryResourceIdentifier {
        // Add a new resource entry to the "type spec chunk" and,
        // a new resource entry to all matching "type chunks"

        val specChunk = this.getTypeSpecChunk(typeName)
        val typeChunks = this.getTypeChunks(typeName)

        // Add a new string to the pool to be used as a key
        val resourceNameIdx = this.keyStringPool.addString(resourceName, /* deduplicate = */ true)

        // Add a new resource entry to the type spec chunk
	    // HACK: Resource index returned by addResource is off by 1 in release builds due to optimizations or something
        val resourceIdx = specChunk.addResource(/* flags = */ 0) + if (BuildConfig.DEBUG) 0 else 1

        for (typeChunk in typeChunks) {
            // If no matching config, add a null entry and try next chunk
            if (!configurations(typeChunk.configuration)) {
                typeChunk.addEntry(null)
                continue
            }

            val entry = TypeChunk.Entry(
                /* headerSize = */ 8,
                /* flags = */ 0,
                /* keyIndex = */ resourceNameIdx,
                /* value = */
                BinaryResourceValue(
                    /* type = */ valueType,
                    /* data = */ valueData,
                ),
                /* values = */ null, // not a complex resource
                /* parentEntry = */ 0, // not a complex resource
                /* parent = */ typeChunk,
            )

            typeChunk.addEntry(entry)
        }

        return BinaryResourceIdentifier.create(
            /* packageId = */ this.id,
            /* typeId = */ specChunk.id,
            /* entryId = */ resourceIdx,
        )
    }

    /**
     * In an arsc file, for a specific resource in a configuration, get it's value.
     *
     * @param resourceId The target resource id.
     * @param configurationName The target configuration variant of the resource. (ex: `anydpi-v26`, `xxhdpi`, `ldtrl-mpi`, etc.)
     * @return The string value of the resource, which should be a file path inside the apk.
     */
    fun ResourceTableChunk.getResourceFileName(
        resourceId: BinaryResourceIdentifier,
        configurationName: String,
    ): String {
        val packageChunk = this.packages.find { it.id == resourceId.packageId() }
            ?: error("Unable to find target resource")

        val typeChunk = packageChunk.getTypeChunks(resourceId.typeId())
            .find { it.configuration.toString() == configurationName }
            ?: error("Unable to find target resource")

        val entry = try {
            typeChunk.getEntry(resourceId.entryId())!!
        } catch (_: Throwable) {
            error("Unable to find target resource")
        }

        if (entry.isComplex || entry.value().type() != BinaryResourceValue.Type.STRING)
            error("Target resource value type is not STRING")

        val valueIdx = entry.value().data()
        val value = this.stringPool.getString(valueIdx)

        return value
    }
}