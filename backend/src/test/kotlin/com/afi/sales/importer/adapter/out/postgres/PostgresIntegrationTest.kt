package com.afi.sales.importer.adapter.out.postgres

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DataJpaTest
@ContextConfiguration(
    initializers = [PostgresIntegrationTest.Companion.Initializer::class],
)
@TestPropertySource(
    properties = [
        "logging.level.root=INFO",
        "logging.level.level.com.afi.sales.importer=DEBUG",
    ],
)
@Tag("Integration")
abstract class PostgresIntegrationTest {

    companion object {
        private val POSTGRESQL_CONTAINER = PostgreSQLContainer("postgres:15.2-alpine")
            .apply {
                start()
            }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(applicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of(
                    "spring.datasource.url=${POSTGRESQL_CONTAINER.jdbcUrl}",
                    "spring.datasource.username=${POSTGRESQL_CONTAINER.username}",
                    "spring.datasource.password=${POSTGRESQL_CONTAINER.password}",
                ).applyTo(applicationContext.environment)
            }
        }
    }
}
