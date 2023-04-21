package com.afi.sales.importer.adapter.out.postgres

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "producers")
class ProducerEntity(
    @Id
    val id: Long,

    @Column(nullable = false)
    var balance: BigDecimal,
)
