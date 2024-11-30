package api.address.report.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ExportRequest(
    @JsonProperty("name") val name: String,
    @JsonProperty("data") val data: String,
)