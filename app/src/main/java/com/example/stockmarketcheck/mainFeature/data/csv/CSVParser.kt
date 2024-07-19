package com.example.stockmarketcheck.mainFeature.data.csv

import java.io.InputStream

// aplicamos una interfaz aca x la misma razon de porq lo hacemos en el repo (q sera llamado x el viewmodel), para q la Implementacion del Repo llame a la interfaz en vez
// de a su implementacion y asi si es q cambiamos y usamos otro parcer q no sea CSVparcer pues no haya que cambiar nada en StockRepositoryImpl (tod0 por seguir principios SOLID)
interface CSVParser<T> {
    suspend fun parse(stream: InputStream): List<T>
}