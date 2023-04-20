package com.afi.sales.importer.domain

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

object TransactionScenarios {
    fun producerSale() = Transaction(
        type = TransactionType.ProducerSale,
        date = ZonedDateTime.of(2023, 3, 10, 13, 30, 2, 0, ZoneId.of("UTC")),
        productDescription = "Pencil",
        value = BigDecimal.valueOf(43, 22),
        salesPersonName = "Walter White",
    )
    fun affiliateSale() = Transaction(
        type = TransactionType.AffiliateSale,
        date = ZonedDateTime.of(2023, 3, 10, 13, 30, 2, 0, ZoneId.of("UTC")),
        productDescription = "Pencil",
        value = BigDecimal.valueOf(43, 22),
        salesPersonName = "Walter White",
    )
}
