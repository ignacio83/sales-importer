package com.afi.sales.importer

import com.afi.sales.importer.adapter.out.postgres.ProducerEntity
import com.afi.sales.importer.adapter.out.postgres.TransactionEntity
import com.afi.sales.importer.adapter.out.postgres.TransactionTypeEntity
import com.afi.sales.importer.domain.Transaction
import jakarta.persistence.EntityManager
import java.math.BigDecimal
import java.util.*
import java.util.stream.Stream
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetProducerBalanceComponentTests(
    @LocalServerPort
    val randomServerPort: Int,
) : ComponentTest() {
    private val restTemplate = TestRestTemplate()

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var platformTransactionManager: PlatformTransactionManager

    @TestFactory
    fun findAll(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val existentProducer: ProducerEntity? = null,
            val url: String,
            val expectedStatusCode: HttpStatusCode,
            val expectedBody: String,
        )
        return Stream.of(
            Scenario(
                name = "should be 200 and return balance when producer exists",
                existentProducer = ProducerEntity(1, BigDecimal.valueOf(7361, 2)),
                url = "http://localhost:$randomServerPort/api/v1/producer/1/balance",
                expectedStatusCode = HttpStatus.OK,
                expectedBody = """{ "value": 73.61 }""",
            ),
            Scenario(
                name = "should be 404 when producer does not exists",
                url = "http://localhost:$randomServerPort/api/v1/producer/1/balance",
                expectedStatusCode = HttpStatus.NOT_FOUND,
                expectedBody = """{
                        "detail":"Producer 1 not found",
                        "instance":"/api/v1/producer/1/balance",
                        "status":404,
                        "title":"Not Found",
                        "type":"about:blank"
                    }""",
            ),
        ).map { test ->
            val transactionTemplate = TransactionTemplate(platformTransactionManager)
            DynamicTest.dynamicTest(test.name) {
                given {
                    test.existentProducer?.let { entity ->
                        transactionTemplate.execute { _ ->
                            entityManager.persist(entity)
                        }
                    }
                } whenever {
                    restTemplate.getForEntity<String>(test.url)
                } then {
                    try {
                        assertThat(it.statusCode).isEqualTo(test.expectedStatusCode)
                        assertThatJson(it.body!!).isEqualTo(test.expectedBody)
                    } finally {
                        test.existentProducer?.let { entity ->
                            transactionTemplate.execute { _ ->
                                entityManager.find(ProducerEntity::class.java, entity.id).let(entityManager::remove)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            productDescription = transaction.productDescription,
            salesPersonName = transaction.salesPersonName,
            value = transaction.value,
            date = transaction.date,
            type = TransactionTypeEntity(transaction.type.id, transaction.type.name),
        )
    }
}
