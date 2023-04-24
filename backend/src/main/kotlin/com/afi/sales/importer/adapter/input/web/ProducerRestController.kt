package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.GetProducerBalance
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/producer")
@Tag(name = "producer")
class ProducerRestController(private val getProducerBalance: GetProducerBalance) {

    @GetMapping("/{id}/balance")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Gets producer balance",
        responses =
        [
            ApiResponse(
                responseCode = "404",
                content = [Content(schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun balance(@PathVariable id: Long) =
        getProducerBalance.getBalance(id).let(::BalanceResource)
}
