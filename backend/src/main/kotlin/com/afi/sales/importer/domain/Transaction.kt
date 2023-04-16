package com.afi.sales.importer.domain

import java.math.BigDecimal
import java.time.ZonedDateTime

data class Transaction(
    private val type: TransactionType,
    private val date: ZonedDateTime,
    private val productDescription: String,
    private val value: BigDecimal,
    private val salesPersonName: String,
)