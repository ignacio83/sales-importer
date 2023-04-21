package com.afi.sales.importer.domain

class BadFormedSalesFileException(filename: String, cause: Throwable) : SalesFileException(
    "File $filename is bad-formed.",
    cause,
)
