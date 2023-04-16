package com.afi.sales.importer.domain

import org.assertj.core.api.Assertions.assertThatList
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.time.ZonedDateTime

class SalesInputStreamTest {
    @Test
    fun `should parse file with success when has content and is correct`() {
        val inputStream = this::class.java.getResource("/sales.txt")!!.openStream()
        val salesInputStream = SalesInputStream("sales.txt", inputStream)

        val sales = salesInputStream.parse()
        val transactions = sales.getTransactions()
        val expectedTransactions = listOf(
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
        )

        assertThatList(transactions).containsAll(expectedTransactions)
        assertThatList(sales.getTransactions()).hasSize(20)
    }

    @Test
    fun `should throw exception when file is invalid`() {
        val inputStream = this::class.java.getResource("/sales_invalid.txt")!!.openStream()
        val salesInputStream = SalesInputStream("sales_invalid.txt", inputStream)

        assertThatThrownBy {
            salesInputStream.parse()
        }.isInstanceOf(BadFormedSalesFileException::class.java)
            .hasMessage("File sales_invalid.txt is bad-formed")
            .hasCauseInstanceOf(NumberFormatException::class.java)
    }
}
