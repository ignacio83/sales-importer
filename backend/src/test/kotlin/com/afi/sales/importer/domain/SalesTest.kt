package com.afi.sales.importer.domain

import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class SalesTest {

    @TestFactory
    fun transactionsCount(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val transactions: List<Transaction>,
            val expectedTransactionCount: Int = 0,
        )
        return Stream.of(
            Scenario(
                name = "should count transaction when is empty",
                transactions = emptyList(),
                expectedTransactionCount = 0,
            ),
            Scenario(
                name = "should count transaction when has 2",
                transactions = listOf(
                    TransactionScenarios.producerSale(),
                    TransactionScenarios.affiliateSale(),
                ),
                expectedTransactionCount = 2,
            ),
            Scenario(
                name = "should count transaction when has 3",
                transactions = listOf(
                    TransactionScenarios.producerSale(),
                    TransactionScenarios.producerSale(),
                    TransactionScenarios.affiliateSale(),
                ),
                expectedTransactionCount = 3,
            ),
        ).map { test ->
            dynamicTest(test.name) {
                val sales = Sales()
                given {
                    assertThat(sales.transactionsCount).isEqualTo(0)
                    test.transactions.forEach(sales::addTransaction)
                } whenever {
                    sales.transactionsCount
                } then {
                    assertThat(it).isEqualTo(test.expectedTransactionCount)
                }
            }
        }
    }

}
