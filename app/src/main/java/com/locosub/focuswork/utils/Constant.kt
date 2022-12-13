package com.locosub.focuswork.utils

import android.content.Context
import android.widget.Toast


const val TASK = "task"
const val ADDTASK = "Add Task"
const val UPDATE_TASK = "Update Task"

fun Context.showToast(
    msg: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, msg, duration).show()