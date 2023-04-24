package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.application.port.out.FindAffiliateBalancePort
import com.afi.sales.importer.application.port.out.UpdateAffiliateBalancePort
import com.afi.sales.importer.domain.AffiliateNotFoundException
import java.math.BigDecimal
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

const val AFFILIATE_ID = 1L

@Component
class AffiliatePostgresAdapter(
    private val repository: AffiliateRepository,
) : UpdateAffiliateBalancePort, FindAffiliateBalancePort {
    private val logger = KotlinLogging.logger {}
    override fun sumBalance(balance: BigDecimal) {
        var affiliate = repository.findByIdOrNull(AFFILIATE_ID)
        val insert = affiliate == null
        if (insert) {
            affiliate = AffiliateEntity(AFFILIATE_ID, BigDecimal.ZERO)
        }
        affiliate!!.balance += balance
        repository.save(affiliate!!)
        logger.debug {
            "Affiliate(id=${affiliate.id}) ${if (insert) "inserted" else "updated"}"
        }
    }

    override fun findBalance(affiliateId: Long): BigDecimal {
        val affiliate = repository.findByIdOrNull(affiliateId)
        if (affiliate != null) {
            return affiliate.balance
        } else {
            throw AffiliateNotFoundException(affiliateId)
        }
    }
}
