package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.domain.Transaction

class TransactionPostgresAdapter : InsertTransactionPort {
    override fun insertTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }
}
