package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.FindAllTransactions
import com.afi.sales.importer.domain.TransactionScenarios
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.lang.NullPointerException
import java.util.stream.Stream
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TransactionRestController::class)
class TransactionRestControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var findAllTransactions: FindAllTransactions

    @TestFactory
    fun upload(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val given: (FindAllTransactions) -> (Unit),
            val expected: (ResultActionsDsl) -> (Unit),
        )
        return Stream.of(
            Scenario(
                name = "should be status 200 with results when exists transactions",
                given = {
                    every { it.findAll() } returns listOf(
                        TransactionScenarios.affiliateSale(),
                    )
                },
                expected = {
                    it.andExpect { status { isOk() } }
                        .andExpect {
                            content {
                                json(
                                    """[{
                                  "type": "AffiliateSale",
                                  "productDescription": "Pencil", 
                                  "value": 38.76,
                                  "salesPersonName": "Walter White",
                                  "date":"2023-03-10T13:30:02Z"
                                }]""",
                                )
                            }
                        }
                },
            ),
            Scenario(
                name = "should be status 500 when a unexpected error occurs",
                given = {
                    every { it.findAll() } throws NullPointerException()
                },
                expected = {
                    it.andExpect { status { isInternalServerError() } }
                        .andExpect { content { string("") } }
                },
            ),
        ).map { test ->
            dynamicTest(test.name) {
                given {
                    test.given(findAllTransactions)
                } whenever {
                    mvc.get("/api/v1/sales/transactions")
                } then {
                    test.expected(it)
                }
            }
        }
    }
}
