package com.afi.sales.importer.domain

open class BusinessException(message: String, cause: Throwable? = null) : Exception(message, cause)
