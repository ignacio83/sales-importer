package com.afi.sales.importer.application.port.input

import com.afi.sales.importer.domain.Transaction

interface FindAllTransactions {

    fun findAll(): List<Transaction>
}
