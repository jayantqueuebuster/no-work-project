package com.locosub.focuswork.features.domain.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.locosub.focus_work.common.CommonButton
import com.locosub.focus_work.common.LoadingDialog
import com.locosub.focus_work.common.noRippleEffect
import com.locosub.focuswork.R
import com.locosub.focuswork.data.models.Questions
import com.locosub.focuswork.features.domain.ui.MainViewModel
import com.locosub.focuswork.features.domain.ui.TaskEvents
import com.locosub.focuswork.features.domain.ui.TaskUiEvent
import com.locosub.focuswork.ui.theme.*
import com.locosub.focuswork.utils.showToast
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegressionScreen(
    viewModel: MainViewModel = hiltViewModel()
) {

    val res = viewModel.questionsResponse.value
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.deleteQuestionEventFlow.collectLatest {
            isLoading = when (it) {
                is TaskUiEvent.Success -> {
                    false
                }
                is TaskUiEvent.Failure -> {
                    context.showToast(it.msg.message ?: "something went wrong")
                    false
                }
                TaskUiEvent.Loading -> {
                    true
                }
            }
        }
    }

    if (isLoading)
        LoadingDialog()

    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    text = stringResource(id = R.string.regression_screen),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrey)
        ) {

            if (res.isLoading)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    CircularProgressIndicator()
                }

            if (res.data.isNotEmpty())
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    items(res.data) { data ->
                        ReportEachRow(data = data) {
                            viewModel.onEvent(TaskEvents.DeleteQuestion(data[0].key))
                        }
                    }
                }
        }
    }

}

@Composable
fun ReportEachRow(
    data: List<Questions.QuestionResponse>,
    onClick: () -> Unit
) {

    var iconState by remember { mutableStateOf(false) }
    val icon = if (iconState) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleEffect {
                iconState = !iconState
            }
            .padding(horizontal = 10.dp, vertical = 3.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data[0].key, style = TextStyle(
                        color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(CenterVertically)
                )
                IconButton(onClick = {
                    iconState = !iconState
                }) {
                    Icon(imageVector = icon, contentDescription = "", tint = Orange)
                }
            }

            if (iconState) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightGrey)
                        .height(1.dp)
                )
                data.forEach {
                    QuestionEachRow(data = it) {
                        onClick()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp), contentAlignment = Center
                ) {
                    CommonButton(title = stringResource(id = R.string.delete), background = Red) {
                        onClick()
                    }
                }
            }
        }
    }

}

@Composable
fun QuestionEachRow(
    data: Questions.QuestionResponse,
    onClick: () -> Unit = {}
) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${data.questions.id}). ${data.questions.question}",
            style = TextStyle(
                color = DarkBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = data.questions.answer,
            style = TextStyle(
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}