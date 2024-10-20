package app.revenge.manager.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Index(
    val latest: Versions
) {

    @Serializable
    data class Versions(
        val alpha: String,
        val beta: String,
        val stable: String
    )

}
