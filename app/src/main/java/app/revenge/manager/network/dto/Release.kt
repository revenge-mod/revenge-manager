package app.revenge.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val versionName: String,
    @SerialName("assets") val assets: List<Asset>
)

@Serializable
data class Asset(
    val name: String,
    @SerialName("browser_download_url") val browserDownloadUrl: String
)
