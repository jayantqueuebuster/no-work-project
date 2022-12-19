//package com.locosub.focuswork
//
//import androidx.compose.animation.*
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.locosub.focuswork.service.ServiceHelper
//import com.locosub.focuswork.service.StopwatchService
//import com.locosub.focuswork.service.StopwatchState
//import com.locosub.focuswork.ui.theme.Blue
//import com.locosub.focuswork.ui.theme.Light
//import com.locosub.focuswork.ui.theme.Red
//import com.locosub.focuswork.utils.ACTION_SERVICE_CANCEL
//import com.locosub.focuswork.utils.ACTION_SERVICE_START
//import com.locosub.focuswork.utils.ACTION_SERVICE_STOP
//
//@ExperimentalAnimationApi
//@Composable
//fun MainScreen(stopwatchService: StopwatchService) {
//    val context = LocalContext.current
//    val hours by stopwatchService.hours
//    val minutes by stopwatchService.minutes
//    val seconds by stopwatchService.seconds
//    val currentState by stopwatchService.currentState
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .padding(30.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Column(
//            modifier = Modifier.weight(weight = 9f),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            AnimatedContent(targetState = hours, transitionSpec = { addAnimation() }) {
//                Text(
//                    text = hours,
//                    style = TextStyle(
//                        fontSize = MaterialTheme.typography.h1.fontSize,
//                        fontWeight = FontWeight.Bold,
//                        color = if (hours == "00") Color.White else Blue
//                    )
//                )
//            }
//            AnimatedContent(targetState = minutes, transitionSpec = { addAnimation() }) {
//                Text(
//                    text = minutes, style = TextStyle(
//                        fontSize = MaterialTheme.typography.h1.fontSize,
//                        fontWeight = FontWeight.Bold,
//                        color = if (minutes == "00") Color.White else Blue
//                    )
//                )
//            }
//            AnimatedContent(targetState = seconds, transitionSpec = { addAnimation() }) {
//                Text(
//                    text = seconds, style = TextStyle(
//                        fontSize = MaterialTheme.typography.h1.fontSize,
//                        fontWeight = FontWeight.Bold,
//                        color = if (seconds == "00") Color.White else Blue
//                    )
//                )
//            }
//        }
//        Row(modifier = Modifier.weight(weight = 1f)) {
//            Button(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight(0.8f),
//                onClick = {
//                    ServiceHelper.triggerForegroundService(
//                        context = context,
//                        action = if (currentState == StopwatchState.Started) ACTION_SERVICE_STOP
//                        else ACTION_SERVICE_START
//                    )
//                }, colors = ButtonDefaults.buttonColors(
//                    backgroundColor = if (currentState == StopwatchState.Started) Red else Blue,
//                    contentColor = Color.White
//                )
//            ) {
//                Text(
//                    text = if (currentState == StopwatchState.Started) "Stop"
//                    else if ((currentState == StopwatchState.Stopped)) "Resume"
//                    else "Start"
//                )
//            }
//            Spacer(modifier = Modifier.width(30.dp))
//            Button(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight(0.8f),
//                onClick = {
//                    ServiceHelper.triggerForegroundService(
//                        context = context, action = ACTION_SERVICE_CANCEL
//                    )
//                },
//                enabled = seconds != "00" && currentState != StopwatchState.Started,
//                colors = ButtonDefaults.buttonColors(disabledBackgroundColor = Light)
//            ) {
//                Text(text = "Cancel")
//            }
//        }
//    }
//}
//
