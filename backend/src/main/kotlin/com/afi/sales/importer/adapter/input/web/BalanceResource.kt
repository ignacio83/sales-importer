package com.afi.sales.importer.adapter.input.web

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Balance")
data class BalanceResource(
    @Schema(description = "Value", readOnly = true, nullable = false, example = "10.99")
    val value: BigDecimal,
)
