package com.afi.sales.importer.domain

import java.util.LinkedList

class Sales {
    private val transactionsInner = LinkedList<Transaction>()

    fun addTransaction(transaction: Transaction) {
        transactionsInner.add(transaction)
    }

    val transactions: List<Transaction>
        get() = transactionsInner

    val transactionsCount: Int
        get() = transactions.size
}
