package api.address.report.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExportsVM(
    @SerialName("files") val files: List<ExportVM>,
)

@Serializable
data class ExportVM(
    val id: String,
)

data class ExportResponse(
    val id: String,
    val data: String,
)