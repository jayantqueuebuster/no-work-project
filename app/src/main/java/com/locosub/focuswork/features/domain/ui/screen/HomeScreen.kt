package com.locosub.focus_work.features.domain.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

import com.locosub.focus_work.common.LoadingDialog
import com.locosub.focus_work.common.getActivity
import com.locosub.focuswork.R
import com.locosub.focuswork.data.models.Task
import com.locosub.focuswork.features.domain.ui.AddTaskActivity
import com.locosub.focuswork.features.domain.ui.MainViewModel
import com.locosub.focuswork.features.domain.ui.TaskEvents
import com.locosub.focuswork.features.domain.ui.TaskUiEvent
import com.locosub.focuswork.ui.theme.DarkBlue
import com.locosub.focuswork.ui.theme.LightGrey
import com.locosub.focuswork.ui.theme.Navy
import com.locosub.focuswork.utils.ADDTASK
import com.locosub.focuswork.utils.UPDATE_TASK
import com.locosub.focuswork.utils.showToast
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {

    val context = LocalContext.current.getActivity()!!
    val res = viewModel.taskResponse.value
    var isLoading by remember { mutableStateOf(false) }
    var key by rememberSaveable { mutableStateOf("") }
    if (res.data.isNotEmpty())
        key = res.data[0].key

    LaunchedEffect(key1 = res) {
        if (res.data.isNotEmpty()) {
            res.data.forEach {
                // if (!it.task?.completed!!) {
                viewModel.setTaskData(
                    Task.TaskResponse(
                        Task(
                            it.task?.title ?: "-",
                            it.task?.description ?: "-"
                        ),
                        it.key
                    )
                )
                //  return@LaunchedEffect
                //   }
            }
        }
    }


    LaunchedEffect(key1 = true) {
        viewModel.addTaskDeleteEventFlow.collectLatest {
            isLoading = when (it) {
                is TaskUiEvent.Success -> {
                    context.showToast(it.data)
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
                Text(
                    text = stringResource(id = R.string.focus_work),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, AddTaskActivity::class.java).apply {
                        putExtra("key", ADDTASK)
                    })

                },
                backgroundColor = Navy,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddTask,
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

            if (res.data.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    items(res.data,
                        key = {
                            it.key
                        }
                    ) { data ->
                        //if (!data.task?.completed!!)
                        TaskEachRow(
                            data,
                            onEdit = {
                                val intent = Intent(context, AddTaskActivity::class.java)
                                intent.putExtra("key", UPDATE_TASK)
                                intent.putExtra("data", data)
                                context.startActivity(intent)
                            },
                            selected = data.key == key,
                            onKeyUpdate = { key = it },
                            viewModel = viewModel
                        ) {
                            viewModel.onEvent(
                                TaskEvents.DeleteTask(
                                    data.key
                                )
                            )
                        }
                    }
                }
            }

            if (res.msg.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = res.msg, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }

            if (res.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

        }

    }

    if (isLoading)
        LoadingDialog()

}


@Composable
fun TaskEachRow(
    data: Task.TaskResponse,
    selected: Boolean,
    viewModel: MainViewModel,
    onKeyUpdate: (String) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onKeyUpdate(data.key)
                    viewModel.setTaskData(
                        Task.TaskResponse(
                            Task(
                                data.task?.title ?: "-",
                                data.task?.description ?: "-"
                            ),
                            data.key
                        )
                    )
                }
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.weight(0.8f)
                ) {
                    RadioButton(selected = selected, onClick = {
                        onKeyUpdate(data.key)
                        viewModel.setTaskData(
                            Task.TaskResponse(
                                Task(
                                    data.task?.title ?: "-",
                                    data.task?.description ?: "-"
                                ),
                                data.key
                            )
                        )
                    })
                    Column {
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
                }
                PopUpDialog(
                    edit = { onEdit.invoke() },
                    modifier = Modifier
                        .weight(0.2f)
                        .align(CenterVertically)
                ) {
                    onDelete.invoke()
                }
            }
        }

    }

}

@Composable
fun PopUpDialog(
    modifier: Modifier = Modifier,
    edit: () -> Unit,
    delete: () -> Unit
) {
    var openPopUp by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier.background(color = Color.White)) {
        IconButton(onClick = {
            openPopUp = !openPopUp
        }) {
            Icon(
                painter = painterResource(id = R.drawable.img),
                contentDescription = null
            )
        }

        if (openPopUp) {
            Popup(
                alignment = Alignment.TopStart,
                properties = PopupProperties(
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                ), onDismissRequest = {
                    openPopUp = !openPopUp
                }
            ) {

                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .border(
                            border = BorderStroke(.1.dp, Color.Gray),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(color = Color.White),
                ) {

                    Column(modifier = Modifier.padding(8.dp)) {

                        Row(modifier = Modifier
                            .clickable {
                                edit.invoke()
                                openPopUp = !openPopUp
                            }
                            .padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Text(
                                text = stringResource(R.string.edit),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                        Row(modifier = Modifier
                            .clickable {
                                delete.invoke()
                                openPopUp = !openPopUp
                            }
                            .padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Text(
                                text = stringResource(R.string.delete),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

            }
        }
    }


}
