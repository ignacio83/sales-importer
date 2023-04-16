package com.afi.sales.importer.domain

class BadFormedSalesFileException(filename: String, cause: Throwable) : Exception("File $filename is bad-formed", cause)
