package com.afi.sales.importer.application.port.input

interface ImportSalesFileUseCase {
    fun execute(command: ImportSalesFileCommand): Int
}
