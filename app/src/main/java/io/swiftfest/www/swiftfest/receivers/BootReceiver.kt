package io.swiftfest.www.swiftfest.receivers

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import io.swiftfest.www.swiftfest.utils.NotificationUtils


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            NotificationUtils(context).scheduleMySessionNotifications()
        }
    }
}