package com.omouravictor.invest_view.util

import androidx.navigation.NavController

object NavigationUtil {

    fun clearPileAndNavigateTo(destinationId: Int, navController: NavController) {
        navController.popBackStack(destinationId, true)
        navController.navigate(destinationId)
    }

    fun clearPileAndNavigateToStart(navController: NavController) {
        val startDestination = navController.graph.startDestinationId
        navController.popBackStack(startDestination, true)
        navController.navigate(startDestination)
    }

}