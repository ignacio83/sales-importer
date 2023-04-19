package com.afi.sales.importer.adapter.out.postgres

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionTypeRepository : JpaRepository<TransactionTypeEntity, Int>
