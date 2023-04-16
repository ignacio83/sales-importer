package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImportSalesFileRestController(private val importSalesFileUseCase: ImportSalesFileUseCase) {

    @PostMapping("/sales/upload")
    fun upload(@RequestParam("file") file: MultipartFile) {
        importSalesFileUseCase.execute(ImportSalesFileCommand(file.name, file.inputStream))
    }
}
