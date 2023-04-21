package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImportSalesFileRestController(private val importSalesFileUseCase: ImportSalesFileUseCase) {

    @PostMapping("/api/v1/sales/upload")
    @ResponseStatus(HttpStatus.OK)
    fun upload(@RequestParam("file") file: MultipartFile): UploadSaleFileResponse {
        val count = importSalesFileUseCase.execute(ImportSalesFileCommand(file.originalFilename!!, file.inputStream))
        return UploadSaleFileResponse(transactionsCount = count)
    }
}

data class UploadSaleFileResponse(val transactionsCount: Int)
