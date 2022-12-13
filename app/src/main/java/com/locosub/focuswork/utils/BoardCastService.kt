package com.locosub.focuswork.utils

import android.app.NotificationManager
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification


class BlockNotification : NotificationListenerService() {


    private object ApplicationPackageNames {
        const val FACEBOOK_PACK_NAME = "com.facebook.katana"
        const val FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca"
        const val WHATSAPP_PACK_NAME = "com.whatsapp"
        const val INSTAGRAM_PACK_NAME = "com.instagram.android"
    }

    object InterceptedNotificationCode {
        const val FACEBOOK_CODE = 1
        const val WHATSAPP_CODE = 2
        const val INSTAGRAM_CODE = 3
        const val OTHER_NOTIFICATIONS_CODE = 4 // We ignore all notification with code == 4
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationCode = matchNotificationCode(sbn)
        clearNotofication(notificationCode)
        cancelAllNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val notificationCode = matchNotificationCode(sbn)
        clearNotofication(notificationCode)
        cancelAllNotifications()
    }

    private fun matchNotificationCode(sbn: StatusBarNotification): Int {
        val packageName = sbn.packageName
        return if (packageName == ApplicationPackageNames.FACEBOOK_PACK_NAME || packageName == ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME) {
            InterceptedNotificationCode.FACEBOOK_CODE
        } else if (packageName == ApplicationPackageNames.INSTAGRAM_PACK_NAME) {
            InterceptedNotificationCode.INSTAGRAM_CODE
        } else if (packageName == ApplicationPackageNames.WHATSAPP_PACK_NAME) {
            InterceptedNotificationCode.WHATSAPP_CODE
        } else {
            InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE
        }
    }

    private fun clearNotofication(notificationId: Int) {
        val ns = NOTIFICATION_SERVICE
        val nMgr = applicationContext.getSystemService(ns) as NotificationManager
        nMgr.cancel(notificationId)
    }
}
