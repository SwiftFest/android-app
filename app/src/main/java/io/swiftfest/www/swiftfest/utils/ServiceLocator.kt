package io.swiftfest.www.swiftfest.utils

import com.google.gson.Gson

class ServiceLocator {

    companion object {
        val gson: Gson = Gson()
    }
}
