package com.afi.sales.importer

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@ContextConfiguration(
    initializers = [ComponentTest.Companion.Initializer::class],
)
@TestPropertySource(
    properties = [
        "logging.level.root=INFO",
        "logging.level.level.com.afi.sales.importer=DEBUG",
    ],
)
@Tag("Component")
abstract class ComponentTest {

    companion object {
        private val POSTGRESQL_CONTAINER = PostgreSQLContainer("postgres:15.2-alpine")
            .apply {
                start()
            }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(applicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of(
                    "spring.datasource.url=${POSTGRESQL_CONTAINER.jdbcUrl}",
                ).applyTo(applicationContext.environment)
            }
        }
    }
}
