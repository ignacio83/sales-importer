package com.afi.sales.importer.adapter.input.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImportSalesFileRestController {

    @PostMapping("/sales/upload")
    fun upload(@RequestParam("file") file: MultipartFile) {
    }
}
