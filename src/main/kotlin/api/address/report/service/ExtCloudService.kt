package api.address.report.service

import api.address.report.dto.ExportsVM
import api.address.report.dto.RootExtResponse
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Service
class ExtCloudService {

    @Value("\${external-api.baseUrl}")
    private lateinit var baseUrl: String

    @Value("\${external-api.login}")
    private lateinit var login: String

    @Value("\${external-api.token}")
    private lateinit var password: String

    fun configureGlobalSslContext() {

        val trustAllCertificates = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
            }
        )

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCertificates, java.security.SecureRandom())

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)

        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    fun getRestTemplate(): RestTemplate {
        configureGlobalSslContext()

        return RestTemplate()
    }

    fun getRootExtResponse(): RootExtResponse {
        try {
            val restTemplate = getRestTemplate()

            val credentials = "$login:${password}"
            val authHeader = "Basic " + Base64
                .getEncoder()
                .encodeToString(credentials.toByteArray(StandardCharsets.UTF_8))

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", authHeader)
            }

            val request = HttpEntity(null, headers)
            val uri = URI.create(baseUrl + "api/rp/v1/Exports/Root")

            val resultJSON = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

            when (val statusCode = resultJSON.statusCode) {
                HttpStatus.INTERNAL_SERVER_ERROR -> throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка внешнего поставщика данных."
                )

                HttpStatus.OK -> {
                    if (resultJSON.body == null) {
                        throw ResponseStatusException(
                            HttpStatus.NO_CONTENT, "Запрос выполнен успешно, но внешний источник не передал данные."
                        )
                    }

                    val jsonParser = Json { ignoreUnknownKeys = true }
                    return jsonParser.decodeFromString(RootExtResponse.serializer(), resultJSON.body!!)
                }

                HttpStatus.CREATED -> throw ResponseStatusException(
                    HttpStatus.CREATED, "Запрос выполнен успешно, но внешний источник не передал данные."
                )

                HttpStatus.NO_CONTENT -> throw ResponseStatusException(
                    HttpStatus.NO_CONTENT, "Внешний источник не передал данные."
                )

                else -> {
                    throw ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Необработанный статус: $statusCode"
                    )
                }
            }
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
    }


    fun getIdsExport(id: String): ExportsVM {

        try {
            val restTemplate = getRestTemplate()

            val credentials = "$login:${password}"
            val authHeader = "Basic " + Base64
                .getEncoder()
                .encodeToString(credentials.toByteArray(StandardCharsets.UTF_8))

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", authHeader)
            }

            val request = HttpEntity(null, headers)

            val uri = URI.create(baseUrl + "api/rp/v1/Exports/Folder/${id}/ListFiles?skip=0&take=10")

            val resultJSON = restTemplate.exchange(uri, HttpMethod.GET, request, String::class.java)

            when (val statusCode = resultJSON.statusCode) {
                HttpStatus.INTERNAL_SERVER_ERROR -> throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка внешнего поставщика данных."
                )

                HttpStatus.OK -> {
                    if (resultJSON.body == null) {
                        throw ResponseStatusException(
                            HttpStatus.NO_CONTENT, "Запрос выполнен успешно, но внешний источник не передал данные."
                        )
                    }

                    val jsonParser = Json { ignoreUnknownKeys = true }
                    return jsonParser.decodeFromString(ExportsVM.serializer(), resultJSON.body!!)
                }

                HttpStatus.CREATED -> throw ResponseStatusException(
                    HttpStatus.CREATED, "Запрос выполнен успешно, но внешний источник не передал данные."
                )

                HttpStatus.NO_CONTENT -> throw ResponseStatusException(
                    HttpStatus.NO_CONTENT, "Внешний источник не передал данные."
                )

                else -> {
                    throw ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Необработанный статус: $statusCode"
                    )
                }
            }
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
    }

    fun getFile(id: String): ByteArray {

        try {
            val restTemplate = getRestTemplate()

            val credentials = "$login:${password}"
            val authHeader = "Basic " + Base64
                .getEncoder()
                .encodeToString(credentials.toByteArray(StandardCharsets.UTF_8))

            val headers = HttpHeaders().apply {
                contentType = MediaType.IMAGE_JPEG
                set("Authorization", authHeader)
            }

            val request = HttpEntity(null, headers)

            val uri = URI.create(baseUrl + "download/e/${id}")

            val byteArray = restTemplate.exchange(uri, HttpMethod.GET, request, ByteArray::class.java)

            when (val statusCode = byteArray.statusCode) {
                HttpStatus.INTERNAL_SERVER_ERROR -> throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка внешнего поставщика данных."
                )

                HttpStatus.OK -> {
                    if (byteArray.body == null) {
                        throw ResponseStatusException(
                            HttpStatus.NO_CONTENT, "Запрос выполнен успешно, но внешний источник не передал данные."
                        )
                    }

                    return byteArray.body!!
                }

                HttpStatus.CREATED -> throw ResponseStatusException(
                    HttpStatus.CREATED, "Запрос выполнен успешно, но внешний источник не передал данные."
                )

                HttpStatus.NO_CONTENT -> throw ResponseStatusException(
                    HttpStatus.NO_CONTENT, "Внешний источник не передал данные."
                )

                else -> {
                    throw ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Необработанный статус: $statusCode"
                    )
                }
            }
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }

    }
}