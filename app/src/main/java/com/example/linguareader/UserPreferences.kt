package com.example.linguareader

import android.app.Application

class UserPreferences : Application() {
    var targetLanguage: String? = null
    var lastRead: Int = -1
    var lastStudied: Int = -1

    companion object {
        const val PREFERENCES: String = "preferences"
        const val LAST_READ: String = "lastRead"
        const val LAST_STUDIED: String = "lastStudied"
        const val TARGET_LANGUAGE: String = "targetLanguage"
    }
}
