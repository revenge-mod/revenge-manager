package app.revenge.manager.utils

import androidx.annotation.StringRes
import app.revenge.manager.R
import java.io.Serializable

data class DiscordVersion(
    val major: Int,
    val minor: Int,
    val type: Type
) : Serializable, Comparable<DiscordVersion> {


    enum class Type(val label: String, @StringRes val labelRes: Int) {
        STABLE("Stable", R.string.channel_stable),
        BETA("Beta", R.string.channel_beta),
        ALPHA("Alpha", R.string.channel_alpha),
    }

    override fun compareTo(other: DiscordVersion): Int =
        toVersionCode().toInt() - other.toVersionCode().toInt()

    override fun toString() = "$major.$minor - ${type.label}"

    fun toVersionCode() = "$major${type.ordinal}${if (minor < 10) 0 else ""}${minor}"

    companion object {
        data class LatestInfo(val code: String, val label: String)
        val LATEST = LatestInfo("287013", "287.13 - Stable")

        fun fromVersionCode(string: String): DiscordVersion? = with(string) {
            if (length < 4) return@with null
            if (toIntOrNull() == null) return@with null
            if (toInt() <= 126021) return@with null
            val codeReversed = toCharArray().reversed().joinToString("")
            val typeInt = codeReversed[2].toString().toInt()
            val type = Type.values().getOrNull(typeInt) ?: return@with null
            //force stable channel
            if (type != Type.STABLE) return@with null
            // maintain version below 288
            if (287 < toInt() / 1000)
                fromVersionCode(LATEST.code)
            else
                DiscordVersion(
                    codeReversed.slice(3..codeReversed.lastIndex).reversed().toInt(),
                    codeReversed.substring(0, 2).reversed().toInt(),
                    type
                )
        }

    }

}