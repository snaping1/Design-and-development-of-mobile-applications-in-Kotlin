package com.example.authapp.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val USERS = "users"

    const val USER_ID_ARG = "userId"
    const val USER_DETAIL = "user_detail/{$USER_ID_ARG}"

    fun userDetail(id: Int): String = "user_detail/$id"
}
