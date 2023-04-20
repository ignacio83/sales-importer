package com.afi.sales.importer.domain

import java.lang.NumberFormatException
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatList
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class SalesInputStreamTest {

    @TestFactory
    fun parse(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val filename: String,
            val containsExpectedTransactions: List<Transaction>? = null,
            val expectedTransactionCount: Int = 0,
            val expectedException: KClass<out Exception>? = null,
            val expectedExceptionMessage: String? = null,
            val expectedExceptionCause: KClass<out Exception>? = null,
        )
        return Stream.of(
            Scenario(
                name = "should parse file with success when has content and is correct",
                filename = "sales.txt",
                expectedTransactionCount = 20,
                containsExpectedTransactions = listOf(
                    Transaction(
                        type = TransactionType.ProducerSale,
                        date = ZonedDateTime.parse("2022-01-15T19:20:30-03"),
                        productDescription = "CURSO DE BEM-ESTAR",
                        value = BigDecimal.valueOf(127.50).setScale(2),
                        salesPersonName = "JOSE CARLOS",
                    ),
                    Transaction(
                        type = TransactionType.AffiliateSale,
                        date = ZonedDateTime.parse("2022-02-03T20:51:59-03"),
                        productDescription = "DESENVOLVEDOR FULL STACK",
                        value = BigDecimal.valueOf(1550.00).setScale(2),
                        salesPersonName = "CAROLINA MACHADO",
                    ),
                    Transaction(
                        type = TransactionType.CommissionReceived,
                        date = ZonedDateTime.parse("2022-02-03T20:51:59-03"),
                        productDescription = "DESENVOLVEDOR FULL STACK",
                        value = BigDecimal.valueOf(500.00).setScale(2),
                        salesPersonName = "CAROLINA MACHADO",
                    ),
                    Transaction(
                        type = TransactionType.CommissionPayed,
                        date = ZonedDateTime.parse("2022-02-03T17:23:37-03"),
                        productDescription = "DESENVOLVEDOR FULL STACK",
                        value = BigDecimal.valueOf(500.00).setScale(2),
                        salesPersonName = "ELIANA NOGUEIRA",
                    ),
                    Transaction(
                        type = TransactionType.ProducerSale,
                        date = ZonedDateTime.parse("2022-03-03T13:12:16-03"),
                        productDescription = "DESENVOLVEDOR FULL STACK",
                        value = BigDecimal.valueOf(1550.00).setScale(2),
                        salesPersonName = "ELIANA NOGUEIRA",
                    ),
                ),
            ),
            Scenario(
                name = "should throw exception when file is empty",
                filename = "sales_empty.txt",
                expectedException = EmptySalesFileException::class,
                expectedExceptionMessage = "File sales_empty.txt is empty.",
            ),
            Scenario(
                name = "should throw exception when file is invalid",
                filename = "sales_invalid.txt",
                expectedException = BadFormedSalesFileException::class,
                expectedExceptionMessage = "File sales_invalid.txt is bad-formed.",
                expectedExceptionCause = NumberFormatException::class,
            ),
        ).map { test ->
            dynamicTest(test.name) {
                val inputStream = this::class.java.getResource("/${test.filename}")!!.openStream()
                val salesInputStream = SalesInputStream(test.filename, inputStream)
                if (test.expectedException != null) {
                    val chain = assertThatThrownBy {
                        salesInputStream.parse()
                    }.isInstanceOf(test.expectedException.java).hasMessage(test.expectedExceptionMessage)
                    test.expectedExceptionCause?.let { expectedExceptionCause ->
                        chain.hasCauseInstanceOf(expectedExceptionCause.java)
                    }
                } else {
                    val sales = salesInputStream.parse()
                    val transactions = sales.getTransactions()
                    assertThatList(transactions).containsAll(test.containsExpectedTransactions)
                    assertThatList(sales.getTransactions()).hasSize(test.expectedTransactionCount)
                    assertThat(sales.getTransactionsCount()).isEqualTo(test.expectedTransactionCount)
                }
            }
        }
    }
}
