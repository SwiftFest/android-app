package io.swiftfest.www.swiftfest

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.swiftfest.www.swiftfest.utils.NotificationUtils

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        NotificationUtils(this).scheduleMySessionNotifications()
    }
}
