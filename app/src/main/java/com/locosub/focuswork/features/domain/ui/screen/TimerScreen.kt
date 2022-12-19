package com.locosub.focus_work.features.domain.ui.screen

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.locosub.focus_work.common.*
import com.locosub.focus_work.data.repository.PreferenceStore
import com.locosub.focuswork.R
import com.locosub.focuswork.data.models.Task
import com.locosub.focuswork.features.domain.ui.MainViewModel
import com.locosub.focuswork.features.domain.ui.TaskEvents
import com.locosub.focuswork.features.domain.ui.TaskUiEvent
import com.locosub.focuswork.service.BackgroundManager
import com.locosub.focuswork.service.ServiceHelper
import com.locosub.focuswork.service.StopwatchService
import com.locosub.focuswork.service.StopwatchState
import com.locosub.focuswork.ui.theme.*
import com.locosub.focuswork.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@Composable
fun TimerScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    stopwatchService: StopwatchService
) {
    val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    val data = viewModel.taskData.value
    val context = LocalContext.current
    val levels by remember { mutableStateOf(listOf("Level 1", "Level 2", "Level 3")) }
    var levelState by remember { mutableStateOf("Level 1") }
    var time by remember {
        mutableStateOf("")
    }

    var isEnabled by rememberSaveable {
        mutableStateOf(
            true
        )
    }
    val hours by stopwatchService.hours
    val minutes by stopwatchService.minutes
    val seconds by stopwatchService.seconds
    val currentState by stopwatchService.currentState


    LaunchedEffect(key1 = levelState) {
    }

    var isLoading by remember { mutableStateOf(false) }
    var isStartEnabled by rememberSaveable {
        mutableStateOf(
            true
        )
    }
    var startTimer by remember { mutableStateOf(false) }

    val minToLong: Long = if (time.isNotEmpty()) {
        TimeUnit.MINUTES.toMillis(time.toLong())
    } else {
        0
    }

    if (isLoading) LoadingDialog()

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
                    if (isNotificationServiceEnabled(context)) context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    context.showToast(context.getString(R.string.task_completed))
                    navHostController.navigateUp()
                    isLoading = false
                }

            }
        }
    }

    Scaffold(topBar = {
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
                .padding(bottom = 60.dp)
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
                                text = data.task?.title ?: "-", style = TextStyle(
                                    color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.W400
                                )
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = data.task?.description ?: "-", style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )

                        }

                        Row {
                            levels.forEach {
                                CommonRadioButton(
                                    selected = it == levelState, title = it
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
                            AnimatedContent(
                                targetState = hours,
                                transitionSpec = { addAnimation() }) {
                                Text(
                                    text = hours,
                                    style = TextStyle(
                                        fontSize = MaterialTheme.typography.h4.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hours == "00") Color.Black else DarkBlue
                                    )
                                )
                            }
                            AnimatedContent(
                                targetState = minutes,
                                transitionSpec = { addAnimation() }) {
                                Text(
                                    text = minutes, style = TextStyle(
                                        fontSize = MaterialTheme.typography.h4.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = if (minutes == "00") Color.Black else Orange
                                    )
                                )
                            }
                            AnimatedContent(
                                targetState = seconds,
                                transitionSpec = { addAnimation() }) {
                                Text(
                                    text = seconds, style = TextStyle(
                                        fontSize = MaterialTheme.typography.h4.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        color = if (seconds == "00") Color.Black else Navy
                                    )
                                )
                            }
                        }

                    }
                }

                Row(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(0.8f),
                        onClick = {
                            if (!isNotificationServiceEnabled(context)) {
                                context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            } else {
                                ServiceHelper.triggerForegroundService(
                                    context = context,
                                    action = if (currentState == StopwatchState.Started) ACTION_SERVICE_STOP
                                    else ACTION_SERVICE_START
                                )
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (currentState == StopwatchState.Started) Red else Blue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (currentState == StopwatchState.Started) "Stop"
                            else if ((currentState == StopwatchState.Stopped)) "Resume"
                            else "Start"
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(0.8f),
                        onClick = {
                            context.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            ServiceHelper.triggerForegroundService(
                                context = context, action = ACTION_SERVICE_CANCEL
                            )
                        },
                        enabled = seconds != "00" && currentState != StopwatchState.Started,
                        colors = ButtonDefaults.buttonColors(disabledBackgroundColor = Color.Gray)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            }
        }

    }

}


@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@Composable
fun CountDownTimerScreen(
    totalTime: String,
    startTimer: Boolean,
    viewModel: MainViewModel,
    onTimeUpdate: (String) -> Unit,
    onButtonEnable: (Boolean) -> Unit
) {
    var endTime: Long by remember { mutableStateOf(0) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var latestLifecycleEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    viewModel.setBooleanPref(PreferenceStore.isRunning, true)
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            latestLifecycleEvent = event
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    var currentTime: Long by if (totalTime.isNotEmpty()) {
        remember {
            mutableStateOf(
                300000
                //  totalTime.toLong() * 60000
            )
        }
    } else {
        remember { mutableStateOf(0) }
    }
    endTime = System.currentTimeMillis() + currentTime

    val minutes = (currentTime / 1000).toInt() / 60
    val seconds = (currentTime / 1000).toInt() % 60


    val res = java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    if (startTimer)
        LaunchedEffect(key1 = currentTime) {
            if (currentTime > 0) {
                delay(100L)
                currentTime -= 100L
            } else {
                onButtonEnable(true)
                onTimeUpdate("")
            }
        }

    Text(
        text = res, fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Color.Black
    )

    if (latestLifecycleEvent == Lifecycle.Event.ON_STOP) {
        viewModel.setStringPref(
            PreferenceStore.timer,
            currentTime.toString()
        )
        viewModel.setBooleanPref(PreferenceStore.isRunning, false)
        viewModel.setStringPref(PreferenceStore.endTime, endTime.toString())
    }
    if (latestLifecycleEvent == Lifecycle.Event.ON_START) {
        Log.d("main", "onStart: working ")
        endTime = runBlocking {
            viewModel.getStringPref(PreferenceStore.endTime).first()
        }.toLong()
        currentTime = runBlocking {
            viewModel.getStringPref(PreferenceStore.timer).first()
        }.toLong()
        currentTime = endTime - System.currentTimeMillis()
        if (currentTime > 0) {
            currentTime = 0
        }

    }

}


@RequiresApi(Build.VERSION_CODES.M)
private fun isNotificationServiceEnabled(
    context: Context
): Boolean {

    val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    val pkgName = context.packageName
    val flat = Settings.Secure.getString(
        context.contentResolver, ENABLED_NOTIFICATION_LISTENERS
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

fun usageAccessSettingsPage(
    context: Context
) {
    val intent = Intent()
    intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}

private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
    return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}

@SuppressLint("QueryPermissionsNeeded")
private fun getInstalledApps(
    context: Context
): ArrayList<AppModel> {
    val installedAppsList: ArrayList<AppModel> = arrayListOf()
    val packageManager = context.packageManager
    val packs = packageManager.getInstalledPackages(0)
    for (i in packs.indices) {
        val p = packs[i]
        if (!isSystemPackage(p)) {
            val appName = p.applicationInfo.loadLabel(packageManager).toString()
            val icon = p.applicationInfo.loadIcon(packageManager)
            val packages = p.applicationInfo.packageName
            installedAppsList.add(AppModel(appName, icon, packages))
        }
    }
    installedAppsList.sortBy { it.name.capitalized() }
    return installedAppsList
}


data class AppModel(
    val name: String, val icon: Drawable, val packages: String
)

private fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

private fun isAccessGranted(
    context: Context
): Boolean {
    return try {
        val packageManager: PackageManager = context.packageManager
        val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
        var appOpsManager: AppOpsManager? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        }
        var mode = 0
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOpsManager!!.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                applicationInfo.packageName
            )
        }
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

