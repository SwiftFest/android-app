package io.swiftfest.www.swiftfest.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.swiftfest.www.swiftfest.utils.SingletonHolder

class UserAgendaRepo private constructor(context: Context) {
    private val prefsKey = "UserAgenda"
    private val sessionIdsKey = "savedSessionsIds"
    private val sharedPrefs : SharedPreferences = context.getSharedPreferences(prefsKey, MODE_PRIVATE)
    private val savedSessionIds = HashSet<String>()

    init {
        savedSessionIds.addAll(sharedPrefs.getStringSet(sessionIdsKey, HashSet<String>()))
    }

    fun isSessionBookmarked(sessionId : String) : Boolean {
        return savedSessionIds.contains(sessionId)
    }

    fun bookmarkSession(sessionId : String, flag : Boolean) {
        if (flag) {
            savedSessionIds.add(sessionId)
        } else {
            savedSessionIds.remove(sessionId)
        }
        sharedPrefs.edit().putStringSet(sessionIdsKey, savedSessionIds).apply()
    }

    companion object : SingletonHolder<UserAgendaRepo, Context>(::UserAgendaRepo)
}
