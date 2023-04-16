package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import com.afi.sales.importer.domain.SalesInputStream
import org.springframework.stereotype.Service

@Service
class ImportSalesFileService : ImportSalesFileUseCase {
    override fun execute(command: ImportSalesFileCommand): Int {
        val sales = SalesInputStream(command.filename, command.inputStream).parse()
        return sales.getTransactionsCount()
    }
}
