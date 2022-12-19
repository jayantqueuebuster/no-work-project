package com.locosub.focuswork.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.locosub.focuswork.broadCastReciever.ReceiverApplock

class ServiceApplockJobIntent : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        runApplock()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        BackgroundManager.instance?.init(this)?.startService()
        // BackgroundManager.getInstance().init(this).startAlarmManager()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        BackgroundManager.instance?.init(this)?.startService()
        // BackgroundManager.getInstance().init(this).startAlarmManager()
        super.onDestroy()
    }

    private fun runApplock() {
        val endTime = System.currentTimeMillis() + 210
        //    while (System.currentTimeMillis() < endTime) {
        synchronized(this) {
            try {
                val intent = Intent(this, ReceiverApplock::class.java)
                sendBroadcast(intent)
                // wait(endTime - System.currentTimeMillis())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        //   }
    }

    companion object {
        private const val JOB_ID = 15462
        fun enqueueWork(ctx: Context?, work: Intent?) {
            enqueueWork(
                ctx!!,
                ServiceApplockJobIntent::class.java, JOB_ID, work!!
            )
        }
    }
}
