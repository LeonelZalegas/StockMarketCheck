package com.example.stockmarketcheck.mainFeature.data.csv

import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser
    @Inject
    constructor() : CSVParser<CompanyListing> {
        override suspend fun parse(stream: InputStream): List<CompanyListing> {
            val csvReader = CSVReader(InputStreamReader(stream))
            return withContext(Dispatchers.IO) {
                csvReader
                    .readAll() // esto te devuelve 1 List con Arrays de Strings (cada String es 1 columna/field y cada array una fila de la tabla CSV)
                    .drop(1)
                    .mapNotNull { line ->
                        val symbol = line.getOrNull(0)
                        val name = line.getOrNull(1)
                        val exchange = line.getOrNull(2)
                        CompanyListing(
                            name = name ?: return@mapNotNull null,
                            symbol = symbol ?: return@mapNotNull null,
                            exchange = exchange ?: return@mapNotNull null,
                        )
                        // si bien getOrNull te devuelve null si no hay nada,
                        // aca checamos igual y si pues es null decimos que
                        // TODA la linea retorne null y x ende q no se cree el objeto
                    }
                    .also {
                        csvReader.close()
                    }
            }
        }
    }