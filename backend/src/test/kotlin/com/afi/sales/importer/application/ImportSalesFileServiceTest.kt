package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.out.InsertTransactionPort
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Random
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class ImportSalesFileServiceTest {
    private val insertTransactionPort = mockk<InsertTransactionPort>()

    @TestFactory
    fun execute(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val filename: String,
            val given: (InsertTransactionPort) -> (Unit),
            val expectedCount: Int = 0,
            val expectedException: KClass<out Exception>? = null,
        )
        return Stream.of(
            Scenario(
                name = "should import file when is well-formed",
                filename = "sales.txt",
                given = { insertTransactionPort ->
                    every { insertTransactionPort.insertTransaction(any()) } returns Random().nextLong()
                },
                expectedCount = 20,
            ),
            /*Scenario(
                name = "should creates command when filename is empty",
                filename = "sales.txt",
                expectedException = IllegalArgumentException::class,
            ),*/
        ).map { test ->
            DynamicTest.dynamicTest(test.name) {
                given {
                    test.given(insertTransactionPort)
                } whenever {
                    runCatching {
                        val inputStream = this::class.java.getResource("/${test.filename}")!!.openStream()
                        val service = ImportSalesFileService(insertTransactionPort)
                        service.execute(ImportSalesFileCommand(test.filename, inputStream))
                    }
                } then { result ->
                    result.onSuccess {
                        verify(exactly = test.expectedCount) { insertTransactionPort.insertTransaction(any()) }
                        assertThat(it).isEqualTo(test.expectedCount)
                    }.onFailure { throwable ->
                        assertThat(throwable).isInstanceOf(test.expectedException!!.java)
                    }
                }
            }
        }
    }
}
