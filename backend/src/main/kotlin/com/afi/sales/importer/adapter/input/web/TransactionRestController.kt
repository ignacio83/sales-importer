package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.FindAllTransactions
import com.afi.sales.importer.domain.TransactionType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sales/transactions")
@Tag(name = "sales")
class TransactionRestController(private val findAllTransactions: FindAllTransactions) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all transactions")
    fun list() =
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

@Schema(description = "Transaction")
data class TransactionResource(
    @Schema(description = "Type of transaction", readOnly = true, nullable = false)
    val type: TransactionType,

    @Schema(description = "Description of product", readOnly = true, nullable = false)
    val productDescription: String,

    @Schema(description = "Name of sales person", readOnly = true, nullable = false)
    val salesPersonName: String,

    @Schema(description = "Value", readOnly = true, nullable = false)
    val value: BigDecimal,

    @Schema(description = "Date into ISO 8601 format", readOnly = true, nullable = false)
    val date: ZonedDateTime,
)
