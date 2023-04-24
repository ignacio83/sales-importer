package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.out.FindAllTransactionPort
import com.afi.sales.importer.domain.Transaction
import com.afi.sales.importer.domain.TransactionScenarios
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import io.mockk.every
import io.mockk.mockk
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class TransactionQueryTest {
    private val findAllTransactionPort = mockk<FindAllTransactionPort>()

    @TestFactory
    fun execute(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val given: (FindAllTransactionPort) -> (Unit),
            val expectedTransactions: List<Transaction> = emptyList(),
        )
        return Stream.of(
            Scenario(
                name = "should find transactions when there is 2 transactions",
                given = { findAllTransactionPort ->
                    every { findAllTransactionPort.findAll() } returns listOf(
                        TransactionScenarios.commissionPayed(),
                        TransactionScenarios.affiliateSale(),
                    )
                },
                expectedTransactions = listOf(
                    TransactionScenarios.commissionPayed(),
                    TransactionScenarios.affiliateSale(),
                ),
            ),
            Scenario(
                name = "should return empty when there no transactions",
                given = { findAllTransactionPort ->
                    every { findAllTransactionPort.findAll() } returns emptyList()
                },
                expectedTransactions = emptyList(),
            ),
        ).map { test ->
            DynamicTest.dynamicTest(test.name) {
                given {
                    test.given(findAllTransactionPort)
                } whenever {
                    val service = TransactionQuery(findAllTransactionPort)
                    service.findAll()
                } then {
                    assertThat(it).containsExactlyInAnyOrderElementsOf(it)
                }
            }
        }
    }
}
