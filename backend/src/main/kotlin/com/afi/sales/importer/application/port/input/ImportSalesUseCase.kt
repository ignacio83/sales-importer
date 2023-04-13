package com.afi.sales.importer.application.port.input

import com.afi.sales.importer.domain.Sales
import java.io.InputStream

interface ImportSalesUseCase {
    fun execute(sales: Sales)
}
