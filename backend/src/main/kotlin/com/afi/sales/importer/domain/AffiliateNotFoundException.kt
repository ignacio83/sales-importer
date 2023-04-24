package com.afi.sales.importer.domain

class AffiliateNotFoundException(id: Long) : NotFoundException("Affiliate $id not found")
