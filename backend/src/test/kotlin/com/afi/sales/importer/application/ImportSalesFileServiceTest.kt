package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.application.port.out.UpdateAffiliateBalancePort
import com.afi.sales.importer.application.port.out.UpdateProducerBalancePort
import com.afi.sales.importer.domain.EmptySalesFileException
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import java.math.BigDecimal
import java.util.Random
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class ImportSalesFileServiceTest {
    private val insertTransactionPort = mockk<InsertTransactionPort>()
    private val updateProducerBalancePort = mockk<UpdateProducerBalancePort>()
    private val updateAffiliateBalancePort = mockk<UpdateAffiliateBalancePort>()

    @TestFactory
    fun execute(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val filename: String,
            val given: (InsertTransactionPort, UpdateProducerBalancePort, UpdateAffiliateBalancePort) -> (Unit),
            val expectedCount: Int = 0,
            val expectedAffiliateBalance: BigDecimal = BigDecimal.ZERO,
            val expectedProducerBalance: BigDecimal = BigDecimal.ZERO,
            val expectedException: KClass<out Exception>? = null,
        )
        return Stream.of(
            Scenario(
                name = "should import file when is well-formed",
                filename = "sales.txt",
                given = { insertTransactionPort, updateProducerBalancePort, updateAffiliateBalancePort ->
                    every { insertTransactionPort.insertTransaction(any()) } returns Random().nextLong()
                    every { updateProducerBalancePort.sumBalance(any()) } just runs
                    every { updateAffiliateBalancePort.sumBalance(any()) } just runs
                },
                expectedCount = 20,
                expectedProducerBalance = BigDecimal.valueOf(963750, 2),
                expectedAffiliateBalance = BigDecimal.valueOf(154500, 2),
            ),
            Scenario(
                name = "should throw exception when file is empty",
                filename = "sales_empty.txt",
                given = { _, _, _ ->
                    // does nothing
                },
                expectedException = EmptySalesFileException::class,
            ),
        ).map { test ->
            DynamicTest.dynamicTest(test.name) {
                given {
                    test.given(insertTransactionPort, updateProducerBalancePort, updateAffiliateBalancePort)
                } whenever {
                    runCatching {
                        val inputStream = this::class.java.getResource("/${test.filename}")!!.openStream()
                        val service = ImportSalesFileService(
                            insertTransactionPort,
                            updateProducerBalancePort,
                            updateAffiliateBalancePort,
                        )
                        service.execute(ImportSalesFileCommand(test.filename, inputStream))
                    }
                } then { result ->
                    result.onSuccess {
                        verify(exactly = test.expectedCount) { insertTransactionPort.insertTransaction(any()) }
                        assertThat(it).isEqualTo(test.expectedCount)

                        verify { updateProducerBalancePort.sumBalance(test.expectedProducerBalance) }
                        verify { updateAffiliateBalancePort.sumBalance(test.expectedAffiliateBalance) }
                    }.onFailure { throwable ->
                        assertThat(throwable).isInstanceOf(test.expectedException!!.java)
                    }
                }
            }
        }
    }
}
