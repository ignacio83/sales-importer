package com.afi.sales.importer.domain

import java.io.InputStream

class SalesInputStream(private val inputStream: InputStream) {
    fun Parse(): Sales {
        val sales = Sales()
        inputStream.bufferedReader().use {
            sales.AddTransaction(Transaction())
        }
        return sales
    }
}
