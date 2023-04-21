package com.afi.sales.importer.application.port.out

import java.math.BigDecimal

interface UpdateProducerBalancePort {
    fun sumBalance(balance: BigDecimal)
}
