package com.afi.sales.importer.domain

class EmptySalesFileException(filename: String) : SalesFileException("File $filename is empty.")
