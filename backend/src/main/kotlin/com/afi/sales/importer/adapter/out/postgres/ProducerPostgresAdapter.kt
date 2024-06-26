package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.application.port.out.FindProducerBalancePort
import com.afi.sales.importer.application.port.out.UpdateProducerBalancePort
import com.afi.sales.importer.domain.ProducerNotFoundException
import java.math.BigDecimal
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

const val PRODUCER_ID = 1L

@Component
class ProducerPostgresAdapter(
    private val repository: ProducerRepository,
) : UpdateProducerBalancePort, FindProducerBalancePort {
    private val logger = KotlinLogging.logger {}
    override fun sumBalance(balance: BigDecimal) {
        var producer = repository.findByIdOrNull(PRODUCER_ID)
        val insert = producer == null
        if (insert) {
            producer = ProducerEntity(PRODUCER_ID, BigDecimal.ZERO)
        }
        producer!!.balance += balance
        repository.save(producer!!)
        logger.debug {
            "Producer(id=${producer.id}) ${if (insert) "inserted" else "updated"}"
        }
    }

    override fun findBalance(producerId: Long): BigDecimal {
        val producer = repository.findByIdOrNull(producerId)
        if (producer != null) {
            return producer.balance
        } else {
            throw ProducerNotFoundException(producerId)
        }
    }
}
