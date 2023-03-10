package com.eman.weatherproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.room.LocalSource
import com.eman.weatherproject.features.alert.view.AlertFragment
import com.eman.weatherproject.database.repository.Repository
import com.eman.weatherproject.database.repository.RepositoryInterface
import com.eman.weatherproject.utilities.NOTIFICATION_CHANNEL
import com.eman.weatherproject.utilities.NOTIFICATION_ID
import com.eman.weatherproject.utilities.NOTIFICATION_NAME
import com.eman.weatherproject.utilities.SHARED_PREFERENCES

class WeatherWorker(var context: Context, var params: WorkerParameters): Worker(context,params) {

    private var settings: Settings? = null
    private lateinit var repo: RepositoryInterface

    override fun doWork(): Result {
        repo = Repository.getInstance(
           RemoteSource.getInstance(),
            LocalSource.getInstance(context),
            context,
            context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE))
        settings = repo.getSettingsSharedPreferences()

        if(settings?.notification as Boolean) {
            val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
            sendNotification(id)
        }

        return Result.success()
    }

    private fun sendNotification(id: Int) {
        val intent = Intent(applicationContext, AlertFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val titleNotification = applicationContext.getString(R.string.chanelName)
        val subtitleNotification = applicationContext.getString(R.string.channel_description)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)

            .setSmallIcon(R.drawable.weatherphoto)
            .setContentTitle(titleNotification)
            .setContentText(subtitleNotification)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uri)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = Color.RED

            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id, notification.build())
    }
}
