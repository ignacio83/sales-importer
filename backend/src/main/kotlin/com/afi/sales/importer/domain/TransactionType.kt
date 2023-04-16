package com.afi.sales.importer.domain

enum class TransactionType(val digit: Int) {
    ProducerSale(1), AffiliateSale(2), CommissionPayed(3), CommissionReceived(4);

    companion object {
        fun fromDigit(digit: Int): TransactionType {
            return values().first { it.digit == digit }
        }
    }
}
