package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.domain.Transaction
import com.afi.sales.importer.domain.TransactionScenarios
import com.afi.sales.importer.given
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import java.util.stream.Stream
import kotlin.reflect.KClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TransactionPostgresAdapter::class)
class TransactionPostgresAdapterTest : PostgresIntegrationTest() {

    @Autowired
    private lateinit var transactionPostgresAdapter: TransactionPostgresAdapter

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @TestFactory
    fun insertTransaction(): Stream<DynamicTest> {
        val transactionProducerSale = TransactionScenarios.producerSale()

        data class Scenario(
            val name: String,
            val transaction: Transaction,
            val expectedTransaction: TransactionEntity? = null,
            val expectedException: KClass<out Exception>? = null,
        )
        return Stream.of(
            Scenario(
                name = "should insert transaction when is a producer sale and all is set",
                transaction = transactionProducerSale,
                expectedTransaction = TransactionEntity(
                    productDescription = transactionProducerSale.productDescription,
                    salesPersonName = transactionProducerSale.salesPersonName,
                    value = transactionProducerSale.value,
                    date = transactionProducerSale.date,
                    type = TransactionTypeEntity(transactionProducerSale.type.id, transactionProducerSale.type.name),
                ),
            ),
            Scenario(
                name = "should throw exception transaction when product description has 51 characters",
                transaction = transactionProducerSale.copy(productDescription = RandomStringUtils.randomAlphabetic(51)),
                expectedException = DataIntegrityViolationException::class,
            ),
        ).map { test ->
            dynamicTest(test.name) {
                whenever {
                    runCatching {
                        transactionPostgresAdapter.insertTransaction(test.transaction)
                    }
                } then {
                    it.onSuccess { id ->
                        val foundTransaction = entityManager.find(TransactionEntity::class.java, id)
                        assertThat(foundTransaction.id).isNotNull()
                        assertThat(foundTransaction.type.id).isEqualTo(test.expectedTransaction!!.type.id)
                        assertThat(foundTransaction.productDescription)
                            .isEqualTo(test.expectedTransaction.productDescription)
                        assertThat(foundTransaction.salesPersonName).isEqualTo(test.expectedTransaction.salesPersonName)
                        assertThat(foundTransaction.value).isEqualTo(test.expectedTransaction.value)
                        assertThat(foundTransaction.date).isEqualTo(test.expectedTransaction.date)
                    }.onFailure { throwable ->
                        assertThat(throwable).isInstanceOf(test.expectedException!!.java)
                    }
                }
            }
        }
    }

    @TestFactory
    fun findAll(): Stream<DynamicTest> {
        val transactionProducerSale = TransactionScenarios.producerSale()
        val transactionAffiliateSale = TransactionScenarios.affiliateSale()
        val transactionCommissionPayed = TransactionScenarios.commissionPayed()

        data class Scenario(
            val name: String,
            val existentTransactions: List<TransactionEntity>,
            val expectedTransactions: List<Transaction> = emptyList(),
        )
        return Stream.of(
            Scenario(
                name = "should find transactions when there is 3 transactions",
                existentTransactions = toEntities(
                    transactionProducerSale,
                    transactionAffiliateSale,
                    transactionCommissionPayed,
                ),
                expectedTransactions = listOf(
                    transactionProducerSale,
                    transactionAffiliateSale,
                    transactionCommissionPayed,
                ),
            ),
            Scenario(
                name = "should find nothing when there is no transactions",
                existentTransactions = emptyList(),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                try {
                    given {
                        test.existentTransactions.forEach(entityManager::persistAndFlush)
                    } whenever {
                        transactionPostgresAdapter.findAll()
                    } then {
                        assertThat(it).containsExactlyInAnyOrderElementsOf(test.expectedTransactions)
                    }
                } finally {
                    test.existentTransactions.forEach(entityManager::remove)
                }
            }
        }
    }

    private fun toEntities(vararg transactions: Transaction): List<TransactionEntity> {
        return transactions.map(::toEntity)
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
