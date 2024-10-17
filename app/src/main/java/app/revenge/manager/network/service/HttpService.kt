package app.revenge.manager.network.service

import app.revenge.manager.network.utils.ApiError
import app.revenge.manager.network.utils.ApiFailure
import app.revenge.manager.network.utils.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class HttpService(
    val json: Json,
    val http: HttpClient
) {

    suspend inline fun <reified T> request(builder: HttpRequestBuilder.() -> Unit = {}): ApiResponse<T> {
        var body: String? = null

        val response = try {
            val response = http.request(builder)

            if (response.status.isSuccess()) {
                body = response.bodyAsText()

                if (T::class == String::class) {
                    return ApiResponse.Success(body as T)
                }

                ApiResponse.Success(json.decodeFromString<T>(body))
            } else {
                body = try {
                    response.bodyAsText()
                } catch (e: Throwable) {
                    null
                }

                ApiResponse.Error(ApiError(response.status, body))
            }
        } catch (e: Throwable) {
            ApiResponse.Failure(ApiFailure(e, body))
        }

        return response
    }

}