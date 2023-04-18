package com.afi.sales.importer.adapter.out.postgres

import com.afi.sales.importer.domain.Transaction
import com.afi.sales.importer.domain.TransactionScenarios
import com.afi.sales.importer.then
import com.afi.sales.importer.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import java.util.stream.Stream

@DataJpaTest
@Import(TransactionPostgresAdapter::class)
class TransactionPostgresAdapterTest {

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
            val expectedTransaction: TransactionEntity,
        )
        return Stream.of(
            Scenario(
                name = "should insert a producer sale transaction",
                transaction = transactionProducerSale,
                expectedTransaction = TransactionEntity(
                    productDescription = transactionProducerSale.productDescription,
                    salesPersonName = transactionProducerSale.salesPersonName,
                ),
            ),
        ).map { test ->
            dynamicTest(test.name) {
                whenever {
                    transactionPostgresAdapter.insertTransaction(test.transaction)
                } then { id ->
                    val foundTransaction = entityManager.find(TransactionEntity::class.java, id)
                    assertThat(foundTransaction.id).isNotNull()
                    assertThat(foundTransaction.productDescription).isEqualTo(test.expectedTransaction.productDescription)
                    assertThat(foundTransaction.salesPersonName).isEqualTo(test.expectedTransaction.salesPersonName)
                }
            }
        }
    }
}
