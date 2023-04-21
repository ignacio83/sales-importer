package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
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
import java.math.BigDecimal

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProducerPostgresAdapter::class)
class ProducerPostgresAdapterTest : PostgresIntegrationTest() {

    @Autowired
    private lateinit var producerPostgresAdapter: ProducerPostgresAdapter

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @TestFactory
    fun sumBalance(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val balance: BigDecimal,
            val actualProducer: ProducerEntity? = null,
            val expectedBalance: BigDecimal,
        )
        return Stream.of(
            Scenario(
                name = "should insert producer with balance when producer does not exists",
                balance = BigDecimal.valueOf(2022, 2),
                expectedBalance = BigDecimal.valueOf(2022, 2),
            ),
            Scenario(
                name = "should sum producer balance when producer already exists",
                actualProducer = ProducerEntity(1, BigDecimal.valueOf(1000, 2)),
                balance = BigDecimal.valueOf(2022, 2),
                expectedBalance = BigDecimal.valueOf(3022, 2),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                given {
                    test.actualProducer?.let {
                        entityManager.persist(test.actualProducer)
                    }
                }
                whenever {
                    producerPostgresAdapter.sumBalance(test.balance)
                } then {
                    val producerFound = entityManager.find(ProducerEntity::class.java, 1)
                    assertThat(producerFound).isNotNull
                    assertThat(producerFound.balance).isEqualTo(test.expectedBalance)
                    entityManager.remove(producerFound)
                }
            }
        }
    }
}
