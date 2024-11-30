package api.address.report.service.impl

import api.address.report.dto.ExportRequest
import api.address.report.dto.ExportResponse
import api.address.report.service.ExtCloudService
import api.address.report.service.IReportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class IReportServiceImpl() : IReportService {

    @Autowired
    lateinit var extCloudService: ExtCloudService

    override fun getExports(): Array<ExportResponse> {
        try {
            val root = extCloudService.getRootExtResponse()

            val exports: MutableList<ExportResponse> = mutableListOf()

            extCloudService.getIdsExport(root.id).files.forEach { file ->
                val bytes = extCloudService.getFile(file.id)

                exports.add(
                    ExportResponse(
                        id = file.id,
                        data = Base64.getEncoder().encodeToString(bytes)
                    )
                )

            }

            return exports.toTypedArray()
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }


    }

    override fun addExport(exportRequest: ExportRequest): HttpStatus {
        return HttpStatus.OK
    }
}