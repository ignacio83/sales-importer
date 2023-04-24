package com.afi.sales.importer.application.port.out

import java.math.BigDecimal

interface FindProducerBalancePort {
    fun findBalance(producerId: Long): BigDecimal
}
