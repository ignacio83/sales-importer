package com.afi.sales.importer

import java.util.stream.Stream
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UploadSalesFileComponentTests(
    @LocalServerPort
    val randomServerPort: Int,
) : ComponentTest() {
    private val restTemplate = TestRestTemplate()

    @TestFactory
    fun upload(): Stream<DynamicTest> {
        data class Scenario(
            val name: String,
            val filename: String? = null,
            val url: String,
            val expectedStatusCode: HttpStatusCode,
            val expectedBody: String,
        )
        return Stream.of(
            Scenario(
                name = "should be OK when file is well-formed",
                filename = "sales.txt",
                url = "http://localhost:$randomServerPort/api/v1/sales/upload",
                expectedStatusCode = HttpStatus.OK,
                expectedBody = """{
                   "transactionsCount":20
                }
                """.trimMargin(),
            ),
            Scenario(
                name = "should be 400 when file is not provided",
                url = "http://localhost:$randomServerPort/api/v1/sales/upload",
                expectedStatusCode = HttpStatus.BAD_REQUEST,
                expectedBody = """{
                   "type":"about:blank",
                   "title":"Bad Request",
                   "status":400,
                   "detail":"Required part 'file' is not present.",
                   "instance":"/api/v1/sales/upload"
                }
                """.trimMargin(),
            ),
            Scenario(
                name = "should be 422 when file is empty",
                filename = "sales_empty.txt",
                url = "http://localhost:$randomServerPort/api/v1/sales/upload",
                expectedStatusCode = HttpStatus.UNPROCESSABLE_ENTITY,
                expectedBody = """{
                   "type":"about:blank",
                   "title":"Unprocessable Entity",
                   "status":422,
                   "detail":"File sales_empty.txt is empty.",
                   "instance":"/api/v1/sales/upload"
                }
                """.trimMargin(),
            ),
        ).map { test ->
            DynamicTest.dynamicTest(test.name) {
                given {
                    test.filename?.let(::ClassPathResource)
                } whenever { file ->
                    val body = LinkedMultiValueMap<String, Any>().apply {
                        add("file", file)
                    }
                    val headers = HttpHeaders().apply {
                        contentType = MediaType.MULTIPART_FORM_DATA
                    }
                    val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(body, headers)
                    restTemplate.postForEntity<String>(test.url, requestEntity)
                } then {
                    assertThat(it.statusCode).isEqualTo(test.expectedStatusCode)
                    assertThatJson(it.body!!).isEqualTo(test.expectedBody)
                }
            }
        }
    }
}
