package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.FindAllTransactions
import com.afi.sales.importer.domain.TransactionType
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionRestController(private val findAllTransactions: FindAllTransactions) {

    @GetMapping("/api/v1/sales/transactions")
    @ResponseStatus(HttpStatus.OK)
    fun findAll() =
        findAllTransactions.findAll().map {
            TransactionResource(
                type = it.type,
                productDescription = it.productDescription,
                value = it.value,
                salesPersonName = it.salesPersonName,
                date = it.date,
            )
        }
}

data class TransactionResource(
    val type: TransactionType,
    val productDescription: String,
    val salesPersonName: String,
    val value: BigDecimal,
    val date: ZonedDateTime,
)
