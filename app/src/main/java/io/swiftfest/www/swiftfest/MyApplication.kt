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

    companion object {
        val SPEAKER_URL = "https://swiftfest.io/speakers.json"
        val SCHEDULE_URL = "https://swiftfest.io/schedule.json"
        val SESSION_URL = "https://swiftfest.io/sessions.json"
        val VOLUNTEER_URL = "https://swiftfest.io/team.json"
    }
}
