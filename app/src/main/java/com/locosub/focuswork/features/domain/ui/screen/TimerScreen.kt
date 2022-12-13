package com.locosub.focus_work.features.domain.ui.screen

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.locosub.focus_work.common.*
import com.locosub.focuswork.data.models.Task
import com.locosub.focus_work.data.repository.PreferenceStore
import com.locosub.focuswork.R
import com.locosub.focuswork.features.domain.ui.MainViewModel
import com.locosub.focuswork.features.domain.ui.TaskEvents
import com.locosub.focuswork.features.domain.ui.TaskUiEvent
import com.locosub.focuswork.ui.theme.DarkBlue
import com.locosub.focuswork.ui.theme.LightGrey
import com.locosub.focuswork.ui.theme.Navy
import com.locosub.focuswork.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TimerScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController
) {
    val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    val data = viewModel.taskData.value
    val context = LocalContext.current
    val levels by remember { mutableStateOf(listOf("Level 1", "Level 2", "Level 3")) }
    var levelState by remember { mutableStateOf("Level 1") }
    var time by remember { mutableStateOf("") }
    var isEnabled by rememberSaveable {
        mutableStateOf(
            true
//            runBlocking {
//            viewModel.getBooleanPref(PreferenceStore.buttonEnable).first()
//        }
        )
    }
    LaunchedEffect(key1 = levelState) {
        if (!isNotificationServiceEnabled(context) && levelState == "Level 1") {
            context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))

        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var isStartEnabled by rememberSaveable {
        mutableStateOf(
            true
//            runBlocking {
//            viewModel.getBooleanPref(PreferenceStore.startButtonEnabled).first()
//        }
        )
    }
    var startTimer by remember { mutableStateOf(false) }

    val minToLong: Long = if (time.isNotEmpty()) {
        TimeUnit.MINUTES.toMillis(time.toLong())
    } else {
        0
    }

    if (isLoading)
        LoadingDialog()

    LaunchedEffect(key1 = true) {
        viewModel.addTaskUpdateEventFlow.collectLatest {
            when (it) {
                is TaskUiEvent.Failure -> {
                    isLoading = false
                    context.showToast(
                        it.msg.message ?: context.getString(R.string.could_not_update_task)
                    )
                }
                TaskUiEvent.Loading -> {
                    isLoading = true
                }
                is TaskUiEvent.Success -> {
                    if (isNotificationServiceEnabled(context))
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    context.showToast(context.getString(R.string.task_completed))
                    navHostController.navigateUp()
                    isLoading = false
                }

            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    text = stringResource(id = R.string.timer_screen),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrey)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGrey)
                        .padding(bottom = 60.dp)
                ) {
                    Column {

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = data.task?.title ?: "-",
                                style = TextStyle(
                                    color = DarkBlue,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.W400
                                )
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = data.task?.description ?: "-",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )

                        }

                        Row {
                            levels.forEach {
                                CommonRadioButton(
                                    selected = it == levelState,
                                    title = it
                                ) { state ->
                                    levelState = state
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row {
                                CommonTextField(
                                    text = time,
                                    label = stringResource(id = R.string.enter_min),
                                    modifier = Modifier.width(150.dp),
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done,
                                    color = Navy,
                                    enable = isEnabled
                                ) {
                                    time = it
                                }
                                CommonButton(
                                    title = stringResource(id = R.string.set),
                                    modifier = Modifier.padding(start = 10.dp),
                                    enable = isEnabled,
                                    background = if (isEnabled) Navy else Color.Gray
                                ) {
                                    if (time.isNotEmpty()) {
                                        isEnabled = false
                                        viewModel.setPref(PreferenceStore.buttonEnable, false)
                                    }
                                }
                            }
                            if (time.isNotEmpty())
                                CountDownTimerScreen(
                                    totalTime = minToLong,
                                    startTimer,
                                    onTimeUpdate = { time = it },
                                    viewModel = viewModel
                                ) {
                                    isEnabled = it
                                    isStartEnabled = it
                                    viewModel.setPref(PreferenceStore.buttonEnable, true)
                                    viewModel.setPref(PreferenceStore.startButtonEnabled, true)
                                    viewModel.onEvent(
                                        TaskEvents.UpdateTask(
                                            Task.TaskResponse(
                                                Task(
                                                    data.task?.title ?: "",
                                                    data.task?.description ?: "",
                                                    true
                                                ),
                                                key = data.key
                                            )
                                        )
                                    )
                                }
                            Spacer(modifier = Modifier.height(20.dp))

                            CommonButton(
                                title = stringResource(id = R.string.start),
                                modifier = Modifier.padding(start = 10.dp),
                                enable = isStartEnabled,
                                background = if (isStartEnabled) Navy else Color.Gray
                            ) {
                                if (time.isNotEmpty()) {
                                    startTimer = true
                                    isEnabled = false
                                    viewModel.setPref(PreferenceStore.buttonEnable, false)
                                    isStartEnabled = false
                                    viewModel.setPref(PreferenceStore.startButtonEnabled, false)
                                }

                            }


                        }

                    }
                }
            }
        }

    }

}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@Composable
fun CountDownTimerScreen(
    totalTime: Long,
    startTimer: Boolean,
    viewModel: MainViewModel,
    onTimeUpdate: (String) -> Unit,
    onButtonEnable: (Boolean) -> Unit
) {

    var currentTime by remember {
        mutableStateOf(totalTime)
    }
    val minutes = (currentTime / 1000).toInt() / 60
    val seconds = (currentTime / 1000).toInt() % 60


    val res = java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)


    if (startTimer)
        LaunchedEffect(key1 = currentTime) {
            viewModel.setStringPref(PreferenceStore.timer, currentTime.toString())

            if (currentTime > 0) {
                delay(100L)
                currentTime -= 100L
            } else {
                onButtonEnable(true)
                onTimeUpdate("")
            }
        }

    Text(
        text = res,
        fontSize = 44.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

}


@RequiresApi(Build.VERSION_CODES.M)
private fun isNotificationServiceEnabled(
    context: Context
): Boolean {

    val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    val pkgName = context.packageName
    val flat = Settings.Secure.getString(
        context.contentResolver,
        ENABLED_NOTIFICATION_LISTENERS
    )
    if (!TextUtils.isEmpty(flat)) {
        val names = flat.split(":").toTypedArray()
        for (i in names.indices) {
            val cn = ComponentName.unflattenFromString(names[i])
            if (cn != null) {
                if (TextUtils.equals(pkgName, cn.packageName)) {
                    return true
                }
            }
        }
    }
    return false
}