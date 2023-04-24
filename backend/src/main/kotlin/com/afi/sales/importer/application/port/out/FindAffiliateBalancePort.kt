package com.afi.sales.importer.application.port.out

import java.math.BigDecimal

interface FindAffiliateBalancePort {
    fun findBalance(affiliateId: Long): BigDecimal
}
