package com.example.stockmarketcheck.mainFeature.data.repository

import com.example.stockmarketcheck.mainFeature.data.csv.CSVParser
import com.example.stockmarketcheck.mainFeature.data.local.StockDatabase
import com.example.stockmarketcheck.mainFeature.data.mapper.toCompanyInfo
import com.example.stockmarketcheck.mainFeature.data.mapper.toCompanyListing
import com.example.stockmarketcheck.mainFeature.data.mapper.toCompanyListingEntity
import com.example.stockmarketcheck.mainFeature.data.remote.StockClient
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyInfo
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import com.example.stockmarketcheck.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl
    @Inject
    constructor(
        private val api: StockClient,
        db: StockDatabase,
        private val companyListingsParser: CSVParser<CompanyListing>,
        private val intradayInfoParser: CSVParser<IntradayInfo>,
    ) : StockRepository {
        private val dao = db.dao

        override suspend fun getCompanyListings(
            fetchFromRemote: Boolean,
            query: String,
        ): Flow<Resource<List<CompanyListing>>> {
            return flow {
                emit(Resource.Loading(true))
                val localListings = dao.searchCompanyListings(query)
                emit(
                    Resource.Success(
                        data = localListings.map { it.toCompanyListing() },
                    ),
                )

                val isDbEmpty = localListings.isEmpty() && query.isBlank()
                val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
                if (shouldJustLoadFromCache) {
                    emit(Resource.Loading(false))
                    return@flow
                }

                val remoteListings =
                    try {
                        val response = api.getListings()
                        companyListingsParser.parse(
                            response.byteStream(),
                        ) // principios SOLID de q 1 funcion tiene que tener 1 proposito unicamente nos hace crear otra clase separada para el parcing
                    } catch (e: IOException) {
                        e.printStackTrace()
                        emit(Resource.Error("Couldn't load data", null))
                        null
                    } catch (e: HttpException) {
                        e.printStackTrace()
                        emit(Resource.Error("Couldn't load data", null))
                        null
                    }

                // usamos el pattern "Single Source of truth", osea podriamos tranquilamente hacer un emit de Listings y chau (coincide q es un Companylisting y tod0)
                // pero este patron nod dicta que usemos la BD local como unica fuente de provicion de datos,
                // por ende limpiamos la BD y colocamos el nuevo listado actualizado desde la API
                remoteListings?.let { listings ->
                    dao.clearCompanyListings()
                    dao.insertCompanyListings(
                        listings.map { it.toCompanyListingEntity() },
                    )
                    emit(
                        Resource.Success(
                            data =
                                dao
                                    .searchCompanyListings("")
                                    .map { it.toCompanyListing() },
                        ),
                    )
                    emit(Resource.Loading(false))
                }
            }
        }

        override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
            return try {
                val response = api.getIntradayInfo(symbol)
                val results = intradayInfoParser.parse(response.byteStream())
                Resource.Success(results)
            } catch (e: IOException) {
                e.printStackTrace()
                Resource.Error(
                    message = "Couldn't load intraday info",
                    null,
                )
            } catch (e: HttpException) {
                e.printStackTrace()
                Resource.Error(
                    message = "Couldn't load intraday info",
                    null,
                )
            }
        }

        // basicamente el unico donde hacemos el parseo Json posta
        override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
            return try {
                val result = api.getCompanyInfo(symbol)
                Resource.Success(result.toCompanyInfo())
            } catch (e: IOException) {
                e.printStackTrace()
                Resource.Error(
                    message = "Couldn't load company info",
                    null,
                )
            } catch (e: HttpException) {
                e.printStackTrace()
                Resource.Error(
                    message = "Couldn't load company info",
                    null,
                )
            }
        }
    }