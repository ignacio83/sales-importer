package com.afi.sales.importer.application

import com.afi.sales.importer.application.port.out.FindAffiliateBalancePort
import com.afi.sales.importer.domain.AffiliateNotFoundException
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class AffiliateQueryTest {
    private val findAffiliateBalancePort = mockk<FindAffiliateBalancePort>()

    @TestFactory
    fun execute(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val given: (FindAffiliateBalancePort) -> (Unit),
            val producerId: Long,
            val expectedBalance: BigDecimal? = null,
            val expectedException: KClass<out Exception>? = null,
        )
        return Stream.of(
            Scenario(
                name = "should find balance",
                given = { findAffiliateBalancePort ->
                    every { findAffiliateBalancePort.findBalance(1) } returns BigDecimal.valueOf(3333, 2)
                },
                producerId = 1,
                expectedBalance = BigDecimal.valueOf(3333, 2),
            ),
            Scenario(
                name = "should not capture when exception when port throws one",
                given = { findAffiliateBalancePort ->
                    every { findAffiliateBalancePort.findBalance(1) } throws AffiliateNotFoundException(1)
                },
                producerId = 1,
                expectedException = AffiliateNotFoundException::class,
            ),
        ).map { test ->
            DynamicTest.dynamicTest(test.name) {
                given {
                    test.given(findAffiliateBalancePort)
                } whenever {
                    val service = AffiliateQuery(findAffiliateBalancePort)
                    runCatching {
                        service.getBalance(test.producerId)
                    }
                } then { result ->
                    result.onSuccess {
                        assertThat(it).isEqualTo(test.expectedBalance)
                    }.onFailure {
                        assertThat(it).isInstanceOf(test.expectedException!!.java)
                    }
                }
            }
        }
    }
}
