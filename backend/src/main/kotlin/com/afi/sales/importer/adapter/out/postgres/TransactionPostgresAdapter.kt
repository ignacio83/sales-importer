package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.domain.Transaction
import org.springframework.stereotype.Component

@Component
class TransactionPostgresAdapter(private val transactionRepository: TransactionRepository) : InsertTransactionPort {
    override fun insertTransaction(transaction: Transaction): Long {
        return TransactionEntity(
            productDescription = transaction.productDescription,
            salesPersonName = transaction.salesPersonName,
            //value = transaction.value
        )
            .let(transactionRepository::save).id!!
    }
}
