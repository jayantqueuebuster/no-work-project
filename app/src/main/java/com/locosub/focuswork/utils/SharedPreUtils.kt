package com.locosub.focuswork.utils

import android.content.Context
import android.content.SharedPreferences


class SharedPrefUtil(context: Context) {
    var cxt: Context? = null
    private val EXTRA_LAST_APP = "EXTRA_LAST_APP"
    private val pref: SharedPreferences
    private val mEditor: SharedPreferences.Editor? = null

    init {
        pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun putString(key: String?, value: String?) {
        pref.edit().putString(key, value).apply()
    }

    fun putInteger(key: String?, value: Int) {
        pref.edit().putInt(key, value).apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String?): String? {
        return pref.getString(key, "")
    }

    fun getInteger(key: String?): Int {
        return pref.getInt(key, 0)
    }

    fun getBoolean(key: String?): Boolean {
        return pref.getBoolean(key, false)
    }

    var lastApp: String?
        get() = getString(EXTRA_LAST_APP)
        set(packageName) {
            putString(EXTRA_LAST_APP, packageName)
        }

    fun clearLastApp() {
        pref.edit().remove(EXTRA_LAST_APP)
    }

    //add apps to locked list
    fun createLockedAppsList(appList: List<String?>) {
        for (i in appList.indices) {
            putString("app_$i", appList[i])
        }
        putInteger("listSize", appList.size)
    }

    //get apps from locked list
    val lockedAppsList: List<String?>
        get() {
            val temp: MutableList<String?> = ArrayList()
            val size = getInteger("listSize")
            for (i in 0 until size) {
                temp.add(getString("app_$i"))
            }
            return temp
        }

    var lockedAppsListProfile: List<String?>
        get() {
            val temp: MutableList<String?> = ArrayList()
            val size = getInteger("profileListSize")
            for (i in 0 until size) {
                temp.add(getString("profileApp_$i"))
            }
            return temp
        }
        set(appList) {
            for (i in appList.indices) {
                putString("profileApp_$i", appList[i])
            }
            putInteger("profileListSize", appList.size)
        }
    var daysList: List<String?>
        get() {
            val temp: MutableList<String?> = ArrayList()
            val size = getInteger("daysListSize")
            for (i in 0 until size) {
                temp.add(getString("day_$i"))
            }
            return temp
        }
        set(daysList) {
            for (i in daysList.indices) {
                putString("day_$i", daysList[i])
            }
            putInteger("daysListSize", daysList.size)
        }

    //start time
    var startTimeHour: String?
        get() = getString("start_hour")
        set(date) {
            putString("start_hour", date)
        }

    var startTimeMinute: String?
        get() = getString("start_minute")
        set(date) {
            putString("start_minute", date)
        }

    //endTime
    var endTimeHour: String?
        get() = getString("end_hour")
        set(date) {
            putString("end_hour", date)
        }

    var endTimeMinute: String?
        get() = getString("end_minute")
        set(date) {
            putString("end_minute", date)
        }

    companion object {
        private const val SHARED_APP_PREFERENCE_NAME = "SharedPref"
        fun getInstance(context: Context): SharedPrefUtil {
            return SharedPrefUtil(context)
        }
    }
}