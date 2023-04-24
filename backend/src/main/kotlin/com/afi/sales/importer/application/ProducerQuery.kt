package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.GetProducerBalance
import com.afi.sales.importer.application.port.out.FindProducerBalancePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProducerQuery(private val findProducerBalancePort: FindProducerBalancePort) : GetProducerBalance {
    @Transactional(readOnly = true)
    override fun getBalance(producerId: Long) = findProducerBalancePort.findBalance(producerId)
}
