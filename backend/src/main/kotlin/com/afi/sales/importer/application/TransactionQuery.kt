package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.FindAllTransactions
import com.afi.sales.importer.application.port.out.FindAllTransactionPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionQuery(
    private val findAllTransactionPort: FindAllTransactionPort,
) : FindAllTransactions {

    @Transactional(readOnly = true)
    override fun findAll() = findAllTransactionPort.findAll()
}
