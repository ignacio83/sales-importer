package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.application.port.out.UpdateAffiliateBalancePort
import com.afi.sales.importer.application.port.out.UpdateProducerBalancePort
import com.afi.sales.importer.domain.SalesInputStream
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ImportSalesFileService(
    private val insertTransactionPort: InsertTransactionPort,
    private val updateProducerBalancePort: UpdateProducerBalancePort,
    private val updateAffiliateBalancePort: UpdateAffiliateBalancePort,
) : ImportSalesFileUseCase {

    @Transactional
    override fun execute(command: ImportSalesFileCommand): Int {
        val sales = SalesInputStream(command.filename, command.inputStream).parse()
        sales.transactions.forEach(insertTransactionPort::insertTransaction)

        updateProducerBalancePort.sumBalance(sales.producerBalance)
        updateAffiliateBalancePort.sumBalance(sales.affiliateBalance)

        return sales.transactionsCount
    }
}
