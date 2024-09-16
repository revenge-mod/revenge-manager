package app.revenge.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val versionName: String
)