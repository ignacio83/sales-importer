package com.afi.sales.importer.domain

import java.math.BigDecimal
import java.time.ZonedDateTime

data class Transaction(
    val type: TransactionType,
    val date: ZonedDateTime,
    val productDescription: String,
    val value: BigDecimal,
    val salesPersonName: String,
)
