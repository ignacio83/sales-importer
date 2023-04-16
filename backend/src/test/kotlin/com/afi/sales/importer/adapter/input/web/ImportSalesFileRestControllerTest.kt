package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.Stream

@WebMvcTest(ImportSalesFileRestController::class)
class ImportSalesFileRestControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var importSalesFileUseCase: ImportSalesFileUseCase

    @TestFactory
    fun upload(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val filename: String? = null,
            val prepare: (ImportSalesFileUseCase) -> (Unit),
            val expectedStatus: ResultMatcher,
            val expectedContent: ResultMatcher,
            val expectedFilename: String? = null,
        )
        return Stream.of(
            Scenario(
                name = "should be status 200 when file is provided",
                filename = "sales.txt",
                prepare = { uc ->
                    every { uc.execute(any()) } returns 10
                },
                expectedStatus = status().isOk,
                expectedContent = content().json(
                    """{
                      "transactionsCount" : 10
                    }
                    """.trimMargin(),
                ),
            ),
            Scenario(
                name = "should be status 400 when file is not provided",
                prepare = {
                    // Does nothing
                },
                expectedStatus = status().isBadRequest,
                expectedContent = content().string(""),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                given {
                    test.prepare(importSalesFileUseCase)
                } whenever {
                    val file = test.filename?.let {
                        val inputStream = this::class.java.getResource("/$it")!!.openStream()
                        MockMultipartFile("file", test.filename, "text/plain", inputStream)
                    }
                    if (file != null) {
                        mvc.perform(multipart("/api/v1/sales/upload").file(file))
                    } else {
                        mvc.perform(multipart("/api/v1/sales/upload"))
                    }
                } then {
                    it.andExpect(test.expectedStatus)
                        .andExpect(test.expectedContent)
                    test.expectedFilename?.let { filename ->
                        val commandSlot = slot<ImportSalesFileCommand>()
                        verify { importSalesFileUseCase.execute(capture(commandSlot)) }
                        assertThat(commandSlot.captured.filename).isEqualTo(filename)
                    }
                }
            }
        }
    }
}
