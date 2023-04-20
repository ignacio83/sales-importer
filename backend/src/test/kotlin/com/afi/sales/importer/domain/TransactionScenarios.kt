package com.afi.sales.importer.domain

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

object TransactionScenarios {

    fun producerSale(value: BigDecimal = BigDecimal.valueOf(4222, 2)) = Transaction(
        type = TransactionType.ProducerSale,
        date = ZonedDateTime.of(2023, 3, 10, 13, 30, 2, 0, ZoneId.of("UTC")),
        productDescription = "Pencil",
        value = value,
        salesPersonName = "Walter White",
    )

    fun affiliateSale(value: BigDecimal = BigDecimal.valueOf(3876, 2)) = Transaction(
        type = TransactionType.AffiliateSale,
        date = ZonedDateTime.of(2023, 3, 10, 13, 30, 2, 0, ZoneId.of("UTC")),
        productDescription = "Pencil",
        value = value,
        salesPersonName = "Walter White",
    )
}
