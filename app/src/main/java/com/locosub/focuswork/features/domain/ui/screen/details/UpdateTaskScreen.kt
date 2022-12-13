package com.locosub.focus_work.features.domain.ui.screen.details

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.locosub.focus_work.common.CommonTextField
import com.locosub.focus_work.common.LoadingDialog
import com.locosub.focus_work.common.getActivity
import com.locosub.focuswork.R
import com.locosub.focuswork.data.models.Task
import com.locosub.focuswork.features.domain.ui.MainViewModel
import com.locosub.focuswork.features.domain.ui.TaskEvents
import com.locosub.focuswork.features.domain.ui.TaskUiEvent
import com.locosub.focuswork.ui.theme.LightGrey
import com.locosub.focuswork.ui.theme.Navy

import com.locosub.focuswork.utils.showToast
import kotlinx.coroutines.flow.collectLatest


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdateTaskScreen(
    data: Task.TaskResponse,
    viewModel: MainViewModel = hiltViewModel()
) {

    var title by remember { mutableStateOf(data.task?.title ?: "") }
    var task by remember { mutableStateOf(data.task?.description ?: "") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current.getActivity()!!
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.addTaskUpdateEventFlow.collectLatest {
            isLoading = when (it) {
                is TaskUiEvent.Success -> {
                    context.showToast(it.data)
                    context.finish()
                    false
                }
                is TaskUiEvent.Failure -> {
                    context.showToast(
                        it.msg.message ?: context.getString(R.string.something_went_wrong)
                    )
                    false
                }
                TaskUiEvent.Loading -> {
                    true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = {
                    context.finish()
                }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "")
                }
                Text(
                    text = stringResource(id = R.string.update_task),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        },
        floatingActionButton = {
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(task))
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(
                            TaskEvents.UpdateTask(
                                Task.TaskResponse(
                                    Task(
                                        title,
                                        task,
                                    ),
                                    data.key
                                )
                            )
                        )
                    },
                    backgroundColor = Navy,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrey)

        ) {
            Column {
                CommonTextField(
                    text = title,
                    label = stringResource(id = R.string.enter_title),
                    modifier = Modifier.fillMaxWidth(),
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ) {
                    title = it

                }

                CommonTextField(
                    text = task,
                    label = stringResource(id = R.string.write_task),
                    modifier = Modifier.fillMaxSize(),
                    imeAction = ImeAction.Default,
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Right)
                    }
                ) {
                    task = it

                }
            }
        }
    }
    if (isLoading)
        LoadingDialog()
}
