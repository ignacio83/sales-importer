package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.GetAffiliateBalance
import com.afi.sales.importer.domain.AffiliateNotFoundException
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.lang.NullPointerException
import java.math.BigDecimal
import java.util.stream.Stream
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get

@WebMvcTest(AffiliateRestController::class)
class AffiliateRestControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var getAffiliateBalance: GetAffiliateBalance

    @TestFactory
    fun upload(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val given: (GetAffiliateBalance) -> (Unit),
            val expected: (ResultActionsDsl) -> (Unit),
        )
        return Stream.of(
            Scenario(
                name = "should be status 200 with balance when consumer exists",
                given = {
                    every { it.getBalance(1) } returns BigDecimal.valueOf(1122, 2)
                },
                expected = {
                    it.andExpect { status { isOk() } }
                        .andExpect {
                            content {
                                json(
                                    """{
                                  "value": 11.22
                                }""",
                                )
                            }
                        }
                },
            ),
            Scenario(
                name = "should be status 404 when consumer does not exists",
                given = {
                    every { it.getBalance(1) } throws AffiliateNotFoundException(1)
                },
                expected = {
                    it.andExpect { status { isNotFound() } }
                        .andExpect {
                            content {
                                json(
                                    """{
                                   "title":"Not Found",
                                   "status":404,
                                   "detail":"Affiliate 1 not found"
                                }""",
                                )
                            }
                        }
                },
            ),
            Scenario(
                name = "should be status 500 when a unexpected error occurs",
                given = {
                    every { it.getBalance(any()) } throws NullPointerException()
                },
                expected = {
                    it.andExpect { status { isInternalServerError() } }
                        .andExpect { content { string("") } }
                },
            ),
        ).map { test ->
            dynamicTest(test.name) {
                given {
                    test.given(getAffiliateBalance)
                } whenever {
                    mvc.get("/api/v1/affiliates/1/balance")
                } then {
                    test.expected(it)
                }
            }
        }
    }
}
