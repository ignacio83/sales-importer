package com.afi.sales.importer.domain

enum class TransactionType(val id: Int) {
    ProducerSale(1), AffiliateSale(2), CommissionPayed(3), CommissionReceived(4);

    companion object {
        fun fromId(id: Int): TransactionType {
            return values().first { it.id == id }
        }
    }
}
