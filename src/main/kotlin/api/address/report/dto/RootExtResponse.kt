package api.address.report.dto

import kotlinx.serialization.Serializable

@Serializable
data class RootExtResponse(
    val id: String,
    val creatorUserId: String,
    val name: String,
    val type: String,
    val size: Long,
)