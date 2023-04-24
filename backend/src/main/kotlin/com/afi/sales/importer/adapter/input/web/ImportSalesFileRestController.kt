package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "sales")
class ImportSalesFileRestController(private val importSalesFileUseCase: ImportSalesFileUseCase) {

    @PostMapping("/api/v1/sales/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Imports sales file",
        responses =
        [
            ApiResponse(
                responseCode = "422",
                description = "When file is invalid",
                content = [Content(schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun upload(@RequestPart("file") file: MultipartFile): UploadSalesFileResponse {
        val count = importSalesFileUseCase.execute(ImportSalesFileCommand(file.originalFilename!!, file.inputStream))
        return UploadSalesFileResponse(transactionsCount = count)
    }
}

@Schema(description = "Sales file upload response")
data class UploadSalesFileResponse(
    @Schema(description = "Qty of transactions imported", nullable = false, readOnly = true)
    val transactionsCount: Int,
)
