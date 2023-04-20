package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.ImportSalesFileCommand
import com.afi.sales.importer.application.port.input.ImportSalesFileUseCase
import com.afi.sales.importer.domain.EmptySalesFileException
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import java.util.stream.Stream
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
            val given: (ImportSalesFileUseCase) -> (Unit),
            val expectedStatus: ResultMatcher,
            val expectedContent: ResultMatcher,
            val expectedFilename: String? = null,
        )
        return Stream.of(
            Scenario(
                name = "should be status 200 when file is provided",
                given = { uc ->
                    every { uc.execute(any()) } returns 10
                },
                filename = "sales.txt",
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
                given = {
                    // Does nothing
                },
                expectedStatus = status().isBadRequest,
                expectedContent = content().json(
                    """{
                       "title":"Bad Request",
                       "status":400,
                       "detail":"Required part 'file' is not present."
                    }""",
                ),
            ),
            Scenario(
                name = "should be status 422 when file is empty",
                given = { uc ->
                    every { uc.execute(any()) } throws EmptySalesFileException("sales_empty.tx")
                },
                filename = "sales_empty.txt",
                expectedStatus = status().isUnprocessableEntity,
                expectedContent = content().json(
                    """{
                       "title":"Unprocessable Entity",
                       "status":422,
                       "detail":"File sales_empty.tx is empty."
                    }""",
                ),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                given {
                    test.given(importSalesFileUseCase)
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
