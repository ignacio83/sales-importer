package com.afi.sales.importer.application.port.input

import java.io.InputStream

data class ImportSalesFileCommand(val filename: String, val inputStream: InputStream) {
    init {
        if (filename.isBlank()) {
            throw IllegalArgumentException("filename is required")
        }
    }
}
