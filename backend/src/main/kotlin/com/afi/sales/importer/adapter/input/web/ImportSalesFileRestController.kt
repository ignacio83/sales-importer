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
        description = "Positional file format:<br>" +
            "<table>" +
            "<tr><th>Field</th><th>Begin</th><th>End</th><th>Size</th><th>Description</th></tr>" +
            "<tr><td>Type</td><td>1</td><td>1</td><td>1</td><td>Transaction's type</td></tr>" +
            "<tr><td>Date</td><td>2</td><td>26</td><td>25</td><td>ISO Date + GMT</td></tr>" +
            "<tr><td>Product</td><td>27</td><td>56</td><td>30</td><td>Product's description</td></tr>" +
            "<tr><td>Value</td><td>57</td><td>66</td><td>10</td><td>Transaction value in cents</td></tr>" +
            "<tr><td>Sales person</td><td>67</td><td>86</td><td>20</td><td>Sales person's name</td></tr>" +
            "</table>",
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
