package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.application.port.out.UpdateAffiliateBalancePort
import com.afi.sales.importer.application.port.out.UpdateProducerBalancePort
import com.afi.sales.importer.domain.SalesInputStream
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImportSalesFileService(
    private val insertTransactionPort: InsertTransactionPort,
    private val updateProducerBalancePort: UpdateProducerBalancePort,
    private val updateAffiliateBalancePort: UpdateAffiliateBalancePort,
) : ImportSalesFileUseCase {
    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun execute(command: ImportSalesFileCommand): Int {
        val sales = SalesInputStream(command.filename, command.inputStream).parse()
        sales.transactions.forEach(insertTransactionPort::insertTransaction)

        updateProducerBalancePort.sumBalance(sales.producerBalance)
        updateAffiliateBalancePort.sumBalance(sales.affiliateBalance)

        logger.debug { "File ${command.filename} imported. Total of transactions: ${sales.transactionsCount}" }

        return sales.transactionsCount
    }
}
