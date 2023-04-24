package com.afi.sales.importer.application.port.input

import java.math.BigDecimal

interface GetAffiliateBalance {
    fun getBalance(affiliateId: Long): BigDecimal
}
