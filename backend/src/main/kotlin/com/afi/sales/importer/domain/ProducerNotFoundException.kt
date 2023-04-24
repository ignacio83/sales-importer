package com.afi.sales.importer.domain

class ProducerNotFoundException(id: Long) : NotFoundException("Producer $id not found")
