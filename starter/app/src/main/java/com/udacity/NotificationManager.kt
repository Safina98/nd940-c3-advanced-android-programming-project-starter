package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
private val REQUEST_CODE = 0
fun NotificationManager.cancelNotifications() {
    cancelAll()
}



