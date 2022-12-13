package com.locosub.focuswork.features.domain.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.locosub.focuswork.data.models.Task
import com.locosub.focuswork.features.domain.ui.screen.details.AddTaskScreen
import com.locosub.focus_work.features.domain.ui.screen.details.UpdateTaskScreen
import com.locosub.focus_work.features.navigation.NavigationItems
import com.locosub.focuswork.ui.theme.FocusWorkTheme
import com.locosub.focuswork.utils.ADDTASK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusWorkTheme() {
                val key = intent.extras?.getString("key") ?: "null"
                val data: Task.TaskResponse =
                    intent.extras?.getParcelable("data") ?: Task.TaskResponse()
                AddTaskNavigation(key = key, data)

            }
        }
    }
}

@Composable
fun AddTaskNavigation(
    key: String,
    data: Task.TaskResponse
) {

    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = if (key == ADDTASK) NavigationItems.AddTask.route else NavigationItems.UpdateTask.route
    ) {

        composable(NavigationItems.AddTask.route) {
            AddTaskScreen()
        }

        composable(NavigationItems.UpdateTask.route) {
            UpdateTaskScreen(data)
        }

    }

}