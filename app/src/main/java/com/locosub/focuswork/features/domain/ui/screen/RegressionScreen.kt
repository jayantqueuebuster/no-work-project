package com.locosub.focus_work.features.domain.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.locosub.focuswork.R
import com.locosub.focuswork.ui.theme.DarkBlue
import com.locosub.focuswork.ui.theme.LightGrey
import com.locosub.focuswork.ui.theme.Navy
import com.locosub.focuswork.ui.theme.Orange

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegressionScreen() {

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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
            ) {

                items(5) {
                    ReportEachRow()
                }

            }
        }
    }

}

@Composable
fun ReportEachRow() {

    var iconState by remember { mutableStateOf(false) }
    val icon = if (iconState) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp
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
                    text = "NKDOVESKKDSKDJSS", style = TextStyle(
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
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "1). What is your questions?",
                    style = TextStyle(
                        color = DarkBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "I am good?",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )

            }
        }
    }

}