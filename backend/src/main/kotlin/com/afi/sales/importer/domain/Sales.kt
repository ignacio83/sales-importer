package com.afi.sales.importer.domain

import java.util.LinkedList

class Sales {
    private val transactions = LinkedList<Transaction>()

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    fun getTransactions(): List<Transaction> {
        return transactions
    }

    fun getTransactionsCount() = transactions.size
}
