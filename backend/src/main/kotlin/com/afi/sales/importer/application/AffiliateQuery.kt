package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.GetAffiliateBalance
import com.afi.sales.importer.application.port.out.FindAffiliateBalancePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AffiliateQuery(private val findAffiliateBalancePort: FindAffiliateBalancePort) : GetAffiliateBalance {
    @Transactional(readOnly = true)
    override fun getBalance(affiliateId: Long) = findAffiliateBalancePort.findBalance(affiliateId)
}
