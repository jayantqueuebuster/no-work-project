package com.locosub.focuswork

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.navigation.compose.rememberNavController
import com.locosub.focuswork.features.navigation.BottomBar
import com.locosub.focuswork.features.navigation.MainNavigation
import com.locosub.focuswork.ui.theme.FocusWorkTheme
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusWorkTheme() {
                val navHostController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomBar(navController = navHostController)
                    },
                ) {
                    MainNavigation(navHostController = navHostController)
                }
            }
        }
    }


}
