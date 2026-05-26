package com.example.nobellaureatesclient.presentation.navigation

object NobelDestinations {
    const val AUTH = "auth"
    const val PRIZES_LIST = "prizes_list"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"

    const val PRIZE_DETAILS_ROUTE = "prize_details"
    const val ARG_YEAR = "year"
    const val ARG_CATEGORY = "category"

    fun prizeDetails(year: Int, categoryCode: String): String =
        "$PRIZE_DETAILS_ROUTE/$year/$categoryCode"

    const val PRIZE_DETAILS_PATTERN = "$PRIZE_DETAILS_ROUTE/{$ARG_YEAR}/{$ARG_CATEGORY}"
}
