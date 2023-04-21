package com.afi.sales.importer.adapter.out.postgres

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "transaction_types")
class TransactionTypeEntity(

    @Id
    val id: Int,

    @Column(nullable = false)
    val name: String,
)
