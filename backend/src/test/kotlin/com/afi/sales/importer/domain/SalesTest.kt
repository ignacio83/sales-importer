package com.afi.sales.importer.domain

import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import java.math.BigDecimal
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

    @TestFactory
    fun producerBalance(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val transactions: List<Transaction>,
            val expectedBalance: BigDecimal = BigDecimal.ZERO,
        )
        return Stream.of(
            Scenario(
                name = "should be 0 when transactions is empty",
                transactions = emptyList(),
                expectedBalance = BigDecimal.ZERO,
            ),
            Scenario(
                name = "should be 32.00 when has just one sale transaction for producer and the value is 12.00",
                transactions = listOf(
                    TransactionScenarios.producerSale(value = BigDecimal.valueOf(1200, 2)),
                    TransactionScenarios.affiliateSale(value = BigDecimal.valueOf(2000, 2)),
                ),
                expectedBalance = BigDecimal.valueOf(32.00).setScale(2),
            ),
            Scenario(
                name = "should be 62.22 when sum of transaction sales for producer and affiliate is 42.22",
                transactions = listOf(
                    TransactionScenarios.producerSale(value = BigDecimal.valueOf(1200, 2)),
                    TransactionScenarios.producerSale(value = BigDecimal.valueOf(3022, 2)),
                    TransactionScenarios.affiliateSale(value = BigDecimal.valueOf(2000, 2)),
                ),
                expectedBalance = BigDecimal.valueOf(62.22).setScale(2),
            ),
            Scenario(
                name = "should be 10.22 when sum of transaction sales for producer is 30.22 but there is a commission of 20.00",
                transactions = listOf(
                    TransactionScenarios.producerSale(value = BigDecimal.valueOf(3022, 2)),
                    TransactionScenarios.commissionPayed(value = BigDecimal.valueOf(2000, 2)),
                    TransactionScenarios.commissionReceived(value = BigDecimal.valueOf(2000, 2)),
                ),
                expectedBalance = BigDecimal.valueOf(10.22).setScale(2),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                val sales = Sales()
                given {
                    test.transactions.forEach(sales::addTransaction)
                } whenever {
                    sales.producerBalance
                } then {
                    assertThat(it).isEqualTo(test.expectedBalance)
                }
            }
        }
    }

    @TestFactory
    fun affiliateBalance(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val transactions: List<Transaction>,
            val expectedBalance: BigDecimal = BigDecimal.ZERO,
        )
        return Stream.of(
            Scenario(
                name = "should be 0 when transactions is empty",
                transactions = emptyList(),
                expectedBalance = BigDecimal.ZERO,
            ),
            Scenario(
                name = "should be 0 when has just one sale transaction for affiliate and does not have commission",
                transactions = listOf(
                    TransactionScenarios.producerSale(value = BigDecimal.valueOf(3000, 2)),
                    TransactionScenarios.affiliateSale(value = BigDecimal.valueOf(1111, 2)),
                ),
                expectedBalance = BigDecimal.ZERO,
            ),
            Scenario(
                name = "should be 36.00 when there sum of commission received is 36.00",
                transactions = listOf(
                    TransactionScenarios.affiliateSale(value = BigDecimal.valueOf(5567, 2)),
                    TransactionScenarios.producerSale(value = BigDecimal.valueOf(1200, 2)),
                    TransactionScenarios.commissionReceived(value = BigDecimal.valueOf(1200, 2)),
                    TransactionScenarios.commissionReceived(value = BigDecimal.valueOf(2400, 2)),
                ),
                expectedBalance = BigDecimal.valueOf(36.00).setScale(2),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                val sales = Sales()
                given {
                    test.transactions.forEach(sales::addTransaction)
                } whenever {
                    sales.affiliateBalance
                } then {
                    assertThat(it).isEqualTo(test.expectedBalance)
                }
            }
        }
    }
}
