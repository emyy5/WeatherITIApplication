package com.eman.weatherproject.utilities




enum class MyLanguage(val convertLanguage: String) {
    en ("en"), ar("ar")
}

const val NOTIFICATION_ID = "appName_notification_id"
const val NOTIFICATION_NAME = "appName"
const val NOTIFICATION_CHANNEL = "appName_channel_01"
const val NOTIFICATION_WORK = "appName_notification_work"
const val ENGLISH = true
const val STANDARD = 0
const val ENABLED = true
const val NONE = 0
const val SHARED_PREFERENCES = "My_weather_pre"
const val CURRENT_WEATHER = "My_Current_weather_pre"
const val SAVING_SETTINGS_IN_SHARED_PREFERENCES = "My_setting_pre"
val units = arrayOf("standard","metric","imperial")

