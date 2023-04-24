package com.afi.sales.importer.application.port.input

import java.math.BigDecimal

interface GetProducerBalance {
    fun getBalance(producerId: Long): BigDecimal
}
