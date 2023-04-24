package com.afi.sales.importer

import com.afi.sales.importer.adapter.out.postgres.TransactionEntity
import com.afi.sales.importer.adapter.out.postgres.TransactionTypeEntity
import com.afi.sales.importer.domain.Transaction
import com.afi.sales.importer.domain.TransactionScenarios
import jakarta.persistence.EntityManager
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
class FindAllTransactionsComponentTests(
    @LocalServerPort
    val randomServerPort: Int,
) : ComponentTest() {
    private val restTemplate = TestRestTemplate()

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var platformTransactionManager: PlatformTransactionManager

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @TestFactory
    fun findAll(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val existentTransactions: List<TransactionEntity> = emptyList(),
            val url: String,
            val expectedStatusCode: HttpStatusCode,
            val expectedBody: String,
        )
        return Stream.of(
            Scenario(
                name = "should be 200 and return transactions when exists",
                existentTransactions = listOf(toEntity(TransactionScenarios.commissionPayed())),
                url = "http://localhost:$randomServerPort/api/v1/sales/transactions",
                expectedStatusCode = HttpStatus.OK,
                expectedBody = """[{
                    "type": "CommissionPayed",
                    "productDescription": "Pencil",
                    "value": 10.00,
                    "salesPersonName": "Walter White",
                    "date":"2023-03-10T13:30:02Z"
                }]
                """.trimMargin(),
            ),
            Scenario(
                name = "should be 200 and return empty when no transactions exists",
                url = "http://localhost:$randomServerPort/api/v1/sales/transactions",
                expectedStatusCode = HttpStatus.OK,
                expectedBody = "[]",
            ),
        ).map { test ->
            val transactionTemplate = TransactionTemplate(platformTransactionManager)
            DynamicTest.dynamicTest(test.name) {
                given {
                    transactionTemplate.execute { _ ->
                        test.existentTransactions.forEach(entityManager::persist)
                    }
                } whenever {
                    restTemplate.getForEntity<String>(test.url)
                } then {
                    try {
                        assertThat(it.statusCode).isEqualTo(test.expectedStatusCode)
                        assertThatJson(it.body!!).isEqualTo(test.expectedBody)
                    } finally {
                        transactionTemplate.execute { _ ->
                            test.existentTransactions.forEach { entity ->
                                entityManager.find(TransactionEntity::class.java, entity.id).let(entityManager::remove)
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
