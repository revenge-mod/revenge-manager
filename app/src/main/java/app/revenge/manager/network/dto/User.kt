package app.revenge.manager.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("login") val username: String,
    @SerialName("avatar_url") val avatar: String
)