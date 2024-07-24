package com.example.stockmarketcheck.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.stockmarketcheck.mainFeature.presentation.company_info.CompanyInfoScreen
import com.example.stockmarketcheck.mainFeature.presentation.company_listings.CompanyListingsScreen

// https://www.notion.so/StockMarket-app-fd555472e30c45ef8586565dc35d7d42?pvs=4#374709ebbc634c2abbaa464580d829c3
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = CompanyListings::class) {
        composable<CompanyListings> {
            CompanyListingsScreen(navController = navController)
        }
        composable<CompanyInfo> { backStackEntry ->
            val screenBArgs: CompanyInfo = backStackEntry.toRoute()
            CompanyInfoScreen(screenBArgs.symbol)
        }
    }
}
