package com.afi.sales.importer.adapter.out.postgres

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.ZonedDateTime

@Entity
@Table(name = "transactions")
class TransactionEntity(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    val type: TransactionTypeEntity,

    @Column(nullable = false, length = 50)
    val productDescription: String,

    @Column(nullable = false, length = 50)
    val salesPersonName: String,

    @Column(nullable = false, length = 50)
    val date: ZonedDateTime,

    @Column(nullable = false)
    val value: BigDecimal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
