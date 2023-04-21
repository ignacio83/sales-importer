package com.afi.sales.importer.application.port.out

import java.math.BigDecimal

interface UpdateAffiliateBalancePort {
    fun sumBalance(balance: BigDecimal)
}
