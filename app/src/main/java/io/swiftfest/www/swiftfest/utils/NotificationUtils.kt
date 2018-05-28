package io.swiftfest.www.swiftfest.utils

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.DataProvider
import io.swiftfest.www.swiftfest.data.UserAgendaRepo
import io.swiftfest.www.swiftfest.receivers.BootReceiver
import io.swiftfest.www.swiftfest.receivers.NotificationPublisher
import io.swiftfest.www.swiftfest.views.MainActivity
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


class NotificationUtils(context: Context) : ContextWrapper(context) {

    val dataProvider = DataProvider.instance

    init {
        createChannels()
    }

    @TargetApi(VERSION_CODES.O)
    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create android channel
            val androidChannel = NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true)
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true)
            // Sets the notification light color for notifications posted to this channel
            androidChannel.lightColor = Color.GREEN
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            getNotificationManager().createNotificationChannel(androidChannel)
        }

    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun sendChannelNotification(title: String, body: String, notificationId: Int, channelId: String) {
        val resultIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent
                .FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(applicationContext,
                channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setTicker(getString(R.string.conference_name))
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                // for notification click action, also required on Gingerbread and below
                .setContentIntent(pi)

        getNotificationManager().notify(notificationId, builder.build())
    }

    fun sendAndroidChannelNotification(title: String, body: String, notificationId: Int) {
        sendChannelNotification(title, body, notificationId, ANDROID_CHANNEL_ID)
    }

    fun scheduleMySessionNotifications() {
//        val context = this
//        val userRepo = UserAgendaRepo.getInstance(context)
//        firebaseHelper.eventDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var hasBookmarkedEvents = false
//                for (roomSnapshot in dataSnapshot.children) {
//                    val eventId = roomSnapshot.key
//                    val scheduleEvent = roomSnapshot.getValue(ConferenceDatabase.ScheduleEvent::class.java)
//                    scheduleEvent?.let {
//                        if (userRepo.isSessionBookmarked(eventId)
//                                && scheduleEvent.getLocalStartTime().isAfter(LocalDateTime.now())) {
//                            scheduleEvent.scheduleNotification(context, eventId, scheduleEvent.toScheduleRow(eventId))
//                            hasBookmarkedEvents = true
//                        }
//                    }
//                }
//                enableBootReceiver(context, hasBookmarkedEvents)
//
//                firebaseHelper.eventDatabase.removeEventListener(this)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w("Notification", "scheduleQuery:onCancelled", databaseError.toException())
//
//                firebaseHelper.eventDatabase.removeEventListener(this)
//            }
//        })
    }

    fun scheduleNotificationAlarm(alarmTime: LocalDateTime, sessionId: String, title: String, body: String, sessionDetail: String) {
        if (alarmTime.isAfter(LocalDateTime.now())) {
            val pendingIntent = getAgendaSessionNotificationPendingIntent(sessionId, title, body, sessionDetail)
            val utcInMillis = alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, utcInMillis, pendingIntent)
        }
    }

    fun cancelNotificationAlarm(sessionId: String) {
        val pendingIntent = getAgendaSessionNotificationPendingIntent(sessionId)
        pendingIntent.cancel()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun enableBootReceiver(context: Context, enabled: Boolean = true) {
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm = context.getPackageManager()

        pm.setComponentEnabledSetting(receiver,
                if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    private fun getAgendaSessionNotificationPendingIntent(sessionId: String, title: String = "", body: String = "", sessionDetail: String = ""): PendingIntent {
        val builder = NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)
                .setContentText(title)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title))
                .setSmallIcon(R.drawable.ic_notification_session_start)
                .setAutoCancel(true)

        if (!TextUtils.isEmpty(sessionDetail)) {
            val sessionIntent = MainActivity.getSessionDetailIntent(this, sessionId, sessionDetail)
            val contentIntent = PendingIntent.getActivity(this, 0,
                    sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            builder.setContentIntent(contentIntent)
        }

        val notificationIntent = Intent(this, NotificationPublisher::class.java).apply {
            putExtra(NotificationPublisher.NOTIFICATION_ID, sessionId.hashCode())
            putExtra(NotificationPublisher.SESSION_ID, sessionId)
            putExtra(NotificationPublisher.NOTIFICATION, builder.build())
        }
        val pendingIntent = PendingIntent.getBroadcast(this, sessionId.hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    companion object {
        const val ANDROID_CHANNEL_ID = "io.swiftfest.www.swiftfest.ANDROID"
        const val ANDROID_CHANNEL_NAME = "ANDROID CHANNEL"
    }
}
