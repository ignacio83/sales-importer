package com.afi.sales.importer.adapter.input.web

import com.afi.sales.importer.application.port.input.GetProducerBalance
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/producer")
class ProducerRestController(private val getProducerBalance: GetProducerBalance) {

    @GetMapping("/{id}/balance")
    @ResponseStatus(HttpStatus.OK)
    fun balance(@PathVariable id: Long) =
        getProducerBalance.getBalance(id).let(::BalanceResource)
}
