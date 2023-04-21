package com.afi.sales.importer.domain

import java.math.BigDecimal
import java.util.LinkedList

class Sales {
    private val transactionsInner = LinkedList<Transaction>()
    private var producerBalanceInner = BigDecimal.ZERO
    private var affiliateBalanceInner = BigDecimal.ZERO

    fun addTransaction(transaction: Transaction) {
        transactionsInner.add(transaction)
        when (transaction.type) {
            TransactionType.ProducerSale -> producerBalanceInner += transaction.value
            TransactionType.AffiliateSale -> producerBalanceInner += transaction.value
            TransactionType.CommissionReceived -> affiliateBalanceInner += transaction.value
            TransactionType.CommissionPayed -> producerBalanceInner -= transaction.value
        }
    }

    val transactions: List<Transaction>
        get() = transactionsInner

    val transactionsCount: Int
        get() = transactions.size

    val producerBalance: BigDecimal
        get() = producerBalanceInner

    val affiliateBalance: BigDecimal
        get() = affiliateBalanceInner
}
