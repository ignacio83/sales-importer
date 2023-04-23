package com.afi.sales.importer

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
class WebMvcConfiguration(
    @Value("\${cors.api.allowed.origins}") private val allowedApiOrigin: List<String>,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        allowedApiOrigin.forEach {
            registry.addMapping("/api/v1/**").allowedOrigins(it)
        }
    }
}
