package api.address.report.service

import org.springframework.stereotype.Service

@Service
class ApiKeyService {
    private val apiKeys = mapOf(
        "123e4567-e89b-12d3-a456-426614174000" to "admin",
        "789e1234-e89b-12d3-a456-426614174000" to "user"
    )

    fun validateApiKey(apiKey: String?): Boolean {
        return apiKeys.containsKey(apiKey)
    }

    fun getUserRole(apiKey: String?): String? {
        return apiKeys[apiKey]
    }
}