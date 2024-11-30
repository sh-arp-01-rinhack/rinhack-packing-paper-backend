package api.address.report.controller

import api.address.report.dto.ExportRequest
import api.address.report.dto.ExportResponse
import api.address.report.service.IReportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/report")
class ReportController() {

    @Autowired
    private lateinit var reportService: IReportService

    @PostMapping("/exports")
    @ResponseStatus(HttpStatus.OK)
    fun addExports(@RequestBody exportRequest: ExportRequest): HttpStatus =
        reportService.addExport(exportRequest)
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "При обработке запроса произошла ошибка"
            )

    @GetMapping("/exports")
    @ResponseStatus(HttpStatus.OK)
    fun getExports(): Flux<ExportResponse> =
        Flux.fromIterable(
            reportService.getExports()
                ?.toList()
                ?: throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "При обработке запроса произошла ошибка"
                )
        )

}