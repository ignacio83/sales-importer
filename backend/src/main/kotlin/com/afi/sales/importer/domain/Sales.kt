package com.afi.sales.importer.domain

import java.io.InputStream
import java.util.LinkedList


class Sales {
    private val transactions = LinkedList<Transaction>()

    fun AddTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
}
