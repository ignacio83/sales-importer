package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.domain.Transaction
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TransactionPostgresAdapter(
    private val transactionRepository: TransactionRepository,
    private val transactionTypeRepository: TransactionTypeRepository,
) : InsertTransactionPort {
    private val logger = KotlinLogging.logger {}
    override fun insertTransaction(transaction: Transaction): Long {
        val type = transactionTypeRepository.findById(transaction.type.id).orElseThrow()
        return TransactionEntity(
            type = type,
            productDescription = transaction.productDescription,
            salesPersonName = transaction.salesPersonName,
            value = transaction.value,
            date = transaction.date,
        )
            .let(transactionRepository::save).also {
                logger.debug {
                    "Transaction(type=${type.name},productDescription=${it.productDescription},id=${it.id!!}) inserted"
                }
            }.id!!
    }
}
