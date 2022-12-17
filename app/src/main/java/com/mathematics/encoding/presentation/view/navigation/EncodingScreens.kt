package com.mathematics.encoding.presentation.view.navigation

import android.util.Log
import androidx.annotation.StringRes
import com.mathematics.encoding.R
import com.mathematics.encoding.presentation.model.SymbolWithCode
import com.mathematics.encoding.presentation.model.parseToJson
import com.mathematics.encoding.presentation.view.navigation.EncodingScreens.Result.Arguments

sealed class EncodingScreens(@StringRes val title: Int, val route: String) {
    open val routeWithArgs = route

    object Main : EncodingScreens(title = R.string.app_name, route = "main-screen")

    object Settings : EncodingScreens(title = R.string.settings, route = "settings")

    object Result : EncodingScreens(title = R.string.result, route = "result") {

        override val routeWithArgs = Arguments
            .values
            .joinToString(separator = "", prefix = "$route/") {
                if (it == Arguments.resultList) "{$it}" else "?$it={$it}"
            }

        object Arguments {
            const val resultList = "resultList"
            const val defaultMessage = "defaultMessage"
            const val encodedMessage = "encodedMessage"

            val values = arrayOf(resultList, defaultMessage, encodedMessage)
        }
    }
}


internal fun createRouteToResultScreen(
    resultList: List<SymbolWithCode>,
    defaultMessage: String = "",
    encodedMessage: String = "",
): String {
    val jsonResultList = resultList.parseToJson()
    Log.d("EncodingNavigation", "result screen route = ${EncodingScreens.Result.routeWithArgs}")
    val route = EncodingScreens.Result.route +
            "/$jsonResultList" +
            "?${Arguments.defaultMessage}=$defaultMessage" +
            "?${Arguments.encodedMessage}=${encodedMessage}"
    Log.d("EncodingNavigation", "route = $route")
    return route
}
