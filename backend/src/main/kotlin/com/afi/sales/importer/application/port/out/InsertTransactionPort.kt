package com.afi.sales.importer.application.port.out

import com.afi.sales.importer.domain.Transaction

interface InsertTransactionPort {
    fun insertTransaction(transaction: Transaction): Long
}
