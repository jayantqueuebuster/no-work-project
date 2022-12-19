package com.locosub.focuswork.features.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val icon_focused: ImageVector
) {


    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Rounded.Home,
        icon_focused = Icons.Rounded.Home
    )

    object Timer : BottomBarScreen(
        route = "timer",
        title = "Timer",
        icon = Icons.Rounded.Timer,
        icon_focused = Icons.Rounded.Timer
    )

    object Report : BottomBarScreen(
        route = "report",
        title = "Reflection",
        icon = Icons.Rounded.Note,
        icon_focused = Icons.Rounded.Note
    )

    object Info : BottomBarScreen(
        route = "info",
        title = "Info",
        icon = Icons.Rounded.Info,
        icon_focused = Icons.Rounded.Info
    )

}


sealed class NavigationItems(val route: String) {

    object AddTask : NavigationItems("addTask")
    object UpdateTask : NavigationItems("updateTask")

}