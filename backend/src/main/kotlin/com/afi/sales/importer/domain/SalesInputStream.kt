package com.afi.sales.importer.domain

import java.io.InputStream
import java.math.BigDecimal
import java.time.ZonedDateTime
import mu.KotlinLogging

class SalesInputStream(private val filename: String, private val inputStream: InputStream) {
    private val logger = KotlinLogging.logger {}
    fun parse(): Sales {
        val sales = Sales()
        inputStream.runCatching {
            bufferedReader().use {
                it.lines().map { line ->
                    val type = TransactionType.fromId(line.substring(0, 1).trim().toInt())
                    val date = ZonedDateTime.parse(line.substring(1, 26).trim())
                    val productDescription = line.substring(26, 56).trim()
                    val longValue = line.substring(56, 66).trim().toLong()
                    val value = BigDecimal.valueOf(longValue, 2)
                    val salesPersonName = line.substring(66, line.length).trim()
                    Transaction(
                        type = type,
                        date = date,
                        productDescription = productDescription,
                        value = value,
                        salesPersonName = salesPersonName,
                    )
                }.forEach(sales::addTransaction)
            }
        }.onFailure {
            throw BadFormedSalesFileException(filename, it)
        }.onSuccess {
            if (sales.getTransactions().isEmpty()) {
                throw EmptySalesFileException(filename)
            } else {
                logger.debug { "File $filename read with success. Transactions count: ${sales.getTransactionsCount()}" }
            }
        }
        return sales
    }
}
