package com.afi.sales.importer.application.port.input

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.InputStream
import java.util.stream.Stream
import kotlin.Exception
import kotlin.reflect.KClass

class ImportSalesFileCommandTest {

    @TestFactory
    fun constructor(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val filename: String,
            val inputStream: InputStream = InputStream.nullInputStream(),
            val expectedException: KClass<out Exception>? = null,
        )
        return Stream.of(
            Scenario(
                name = "should creates command when filename is provided",
                filename = "filename.txt",
            ),
            Scenario(
                name = "should creates command when filename is empty",
                filename = "",
                expectedException = IllegalArgumentException::class,
            ),
        ).map { test ->
            dynamicTest(test.name) {
                runCatching {
                    ImportSalesFileCommand(test.filename, test.inputStream)
                }.onFailure { throwable ->
                    assertThat(throwable).isInstanceOf(test.expectedException!!.java)
                }
            }
        }
    }
}
