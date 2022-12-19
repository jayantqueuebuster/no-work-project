package com.locosub.focuswork.service

import android.app.IntentService
import android.content.Intent
import com.locosub.focuswork.broadCastReciever.ReceiverApplock


class ServiceApplock : IntentService("ServiceApplock") {
    private fun runApplock() {
        val endTime = System.currentTimeMillis() + 210
        // while (System.currentTimeMillis() < endTime) {
        synchronized(this) {
            try {
                val intent = Intent(this, ReceiverApplock::class.java)
                sendBroadcast(intent)
                // wait(endTime - System.currentTimeMillis())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        // }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runApplock()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onHandleIntent(intent: Intent?) {}
    override fun onDestroy() {
        BackgroundManager.instance?.init(this)?.startService()
        BackgroundManager.instance?.init(this)?.startAlarmManager()
        super.onDestroy()
    }
}
