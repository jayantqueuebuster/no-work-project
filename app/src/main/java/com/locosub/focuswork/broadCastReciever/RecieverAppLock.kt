package com.locosub.focuswork.broadCastReciever

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.locosub.focuswork.ScreenBlockerActivity
import com.locosub.focuswork.utils.SharedPrefUtil
import com.locosub.focuswork.utils.Utils
import java.time.LocalDate
import java.util.*

class ReceiverApplock : BroadcastReceiver() {
    var currentTime: Calendar? = null
    var fromTime: Calendar? = null
    var toTime: Calendar? = null
    var currentDay: Calendar? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val utils = Utils(context)
        val prefUtil = SharedPrefUtil(context)
        val lockedApps: List<String?> = prefUtil.lockedAppsList
        val appRunning: String = utils.launcherTopApp
        //String lastApp = utils.getLastApp();
        val checkSchedule: Boolean = prefUtil.getBoolean("confirmSchedule")
        val getWeekdaysListString: List<String?> = prefUtil.daysList
        val startTimeHour: String? = prefUtil.startTimeHour
        val startTimeMin: String? = prefUtil.startTimeMinute
        val endTimeHour: String? = prefUtil.endTimeHour
        val endTimeMin: String? = prefUtil.endTimeMinute
        if (checkSchedule) {
            //   if (checkDay(getWeekdaysListString)) {
            //      if (checkTime(startTimeHour, startTimeMin, endTimeHour, endTimeMin)) {
            if (lockedApps.contains(appRunning)) {
                prefUtil.clearLastApp()
                prefUtil.lastApp = appRunning
                killThisPackageIfRunning(context, appRunning)
                val i = Intent(context, ScreenBlockerActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.putExtra("broadcast_receiver", "broadcast_receiver")
                context.startActivity(i)
            }
            //  }
            //  }
        } else {
            //always BLOCK
            if (lockedApps.contains(appRunning)) {
                prefUtil.clearLastApp()
                prefUtil.lastApp = appRunning
                killThisPackageIfRunning(context, appRunning)
                val i = Intent(context, ScreenBlockerActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                i.putExtra("broadcast_receiver", "broadcast_receiver")
                context.startActivity(i)
            }
        }
    }

//    fun checkTime(
//        startTimeHour: String?,
//        startTimeMin: String?,
//        endTimeHour: String?,
//        endTimeMin: String?
//    ): Boolean {
//        try {
//            currentTime = Calendar.getInstance()
//            currentTime.get(Calendar.HOUR_OF_DAY)
//            currentTime.get(Calendar.MINUTE)
//            fromTime = Calendar.getInstance()
//            fromTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimeHour))
//            fromTime.set(Calendar.MINUTE, Integer.valueOf(startTimeMin))
//            fromTime.set(Calendar.SECOND, 0)
//            fromTime.set(Calendar.MILLISECOND, 0)
//            toTime = Calendar.getInstance()
//            toTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTimeHour))
//            toTime.set(Calendar.MINUTE, Integer.valueOf(endTimeMin))
//            toTime.set(Calendar.SECOND, 0)
//            toTime.set(Calendar.MILLISECOND, 0)
//            if (currentTime.after(fromTime) && currentTime.before(toTime)) {
//                return true
//            }
//        } catch (e: Exception) {
//            return false
//        }
//        return false
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun checkDay(weekdays: List<String?>): Boolean {
        currentDay = Calendar.getInstance()
        val today = LocalDate.now().dayOfWeek.name
        return weekdays.contains(today)
    }

    companion object {
        fun killThisPackageIfRunning(context: Context, packageName: String?) {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(startMain)
            activityManager.killBackgroundProcesses(packageName)
        }
    }
}
