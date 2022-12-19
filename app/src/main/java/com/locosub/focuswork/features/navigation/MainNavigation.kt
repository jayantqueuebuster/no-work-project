package com.locosub.focuswork.features.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.locosub.focuswork.features.domain.ui.MainViewModel
import com.locosub.focus_work.features.domain.ui.screen.HomeScreen
import com.locosub.focus_work.features.domain.ui.screen.InfoScreen
import com.locosub.focus_work.features.domain.ui.screen.ReportScreen
import com.locosub.focus_work.features.domain.ui.screen.TimerScreen
import com.locosub.focuswork.service.StopwatchService


@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun MainNavigation(
    navHostController: NavHostController,
    stopwatchService: StopwatchService
) {

    val viewModel: MainViewModel = viewModel()
    NavHost(navController = navHostController, startDestination = BottomBarScreen.Home.route) {
        composable(BottomBarScreen.Home.route) {
            HomeScreen(viewModel)
        }
        composable(BottomBarScreen.Timer.route) {
            TimerScreen(viewModel,navHostController,stopwatchService)
        }
        composable(BottomBarScreen.Report.route) {
            ReportScreen()
        }
        composable(BottomBarScreen.Info.route) {
            InfoScreen()
        }
    }

}