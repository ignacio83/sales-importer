package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import java.math.BigDecimal
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AffiliatePostgresAdapter::class)
class AffiliatePostgresAdapterTest : PostgresIntegrationTest() {

    @Autowired
    private lateinit var affiliatePostgresAdapter: AffiliatePostgresAdapter

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @TestFactory
    fun sumBalance(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val balance: BigDecimal,
            val actualAffiliate: AffiliateEntity? = null,
            val expectedBalance: BigDecimal,
        )
        return Stream.of(
            Scenario(
                name = "should insert affiliate with balance when affiliate does not exists",
                balance = BigDecimal.valueOf(2022, 2),
                expectedBalance = BigDecimal.valueOf(2022, 2),
            ),
            Scenario(
                name = "should sum affiliate balance when affiliate already exists",
                actualAffiliate = AffiliateEntity(1, BigDecimal.valueOf(1000, 2)),
                balance = BigDecimal.valueOf(2022, 2),
                expectedBalance = BigDecimal.valueOf(3022, 2),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                given {
                    test.actualAffiliate?.let {
                        entityManager.persist(test.actualAffiliate)
                    }
                }
                whenever {
                    affiliatePostgresAdapter.sumBalance(test.balance)
                } then {
                    val affiliateFound = entityManager.find(AffiliateEntity::class.java, 1)
                    assertThat(affiliateFound).isNotNull
                    assertThat(affiliateFound.balance).isEqualTo(test.expectedBalance)
                    entityManager.remove(affiliateFound)
                }
            }
        }
    }
}
