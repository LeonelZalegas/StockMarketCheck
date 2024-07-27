package com.example.stockmarketcheck.mainFeature.di

import com.example.stockmarketcheck.mainFeature.data.csv.CSVParser
import com.example.stockmarketcheck.mainFeature.data.csv.CompanyListingsParser
import com.example.stockmarketcheck.mainFeature.data.csv.IntradayInfoParser
import com.example.stockmarketcheck.mainFeature.data.repository.StockRepositoryImpl
import com.example.stockmarketcheck.mainFeature.domain.model.CompanyListing
import com.example.stockmarketcheck.mainFeature.domain.model.IntradayInfo
import com.example.stockmarketcheck.mainFeature.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(companyListingsParser: CompanyListingsParser): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(intradayInfoParser: IntradayInfoParser): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(stockRepositoryImpl: StockRepositoryImpl): StockRepository
}