package api.address.report.service

import api.address.report.dto.ExportRequest
import api.address.report.dto.ExportResponse
import org.springframework.http.HttpStatus

interface IReportService {
    fun getExports(): Array<ExportResponse>?
    fun addExport(exportRequest: ExportRequest): HttpStatus?
}