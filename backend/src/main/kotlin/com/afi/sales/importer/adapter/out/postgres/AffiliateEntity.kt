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
@Table(name = "affiliates")
class AffiliateEntity(
    @Id
    val id: Long,

    @Column(nullable = false)
    var balance: BigDecimal,
)
