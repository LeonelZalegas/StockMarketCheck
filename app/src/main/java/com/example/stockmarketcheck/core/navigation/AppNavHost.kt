package com.example.stockmarketcheck.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.stockmarketcheck.mainFeature.presentation.company_info.CompanyInfoScreenContainer
import com.example.stockmarketcheck.mainFeature.presentation.company_listings.CompanyListingsScreenContainer

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = CompanyListings::class) {
        composable<CompanyListings> {
            CompanyListingsScreenContainer(navController = navController)
        }
        composable<CompanyInfo> { backStackEntry ->
            val screenBArgs: CompanyInfo = backStackEntry.toRoute()
            CompanyInfoScreenContainer(screenBArgs.symbol)
        }
    }
}
