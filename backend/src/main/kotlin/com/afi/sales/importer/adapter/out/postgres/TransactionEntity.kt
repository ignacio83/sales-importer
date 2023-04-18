package com.afi.sales.importer.adapter.out.postgres

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "transactions")
class TransactionEntity(
    @Column(nullable = false, length = 50)
    val productDescription: String,
    //val date : ZonedDateTime,

    //@Column(nullable = false, columnDefinition = "NUMBER(8,2)")
    //val value: BigDecimal,

    @Column(nullable = false, length = 50)
    val salesPersonName: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
