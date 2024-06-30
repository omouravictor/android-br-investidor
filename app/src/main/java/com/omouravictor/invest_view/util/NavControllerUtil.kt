package com.omouravictor.invest_view.util

import androidx.navigation.NavController

fun NavController.clearPileAndNavigateTo(destinationId: Int) {
    this.popBackStack(destinationId, true)
    this.navigate(destinationId)
}

fun NavController.clearPileAndNavigateToStart() {
    val startDestination = this.graph.startDestinationId
    this.popBackStack(startDestination, true)
    this.navigate(startDestination)
}