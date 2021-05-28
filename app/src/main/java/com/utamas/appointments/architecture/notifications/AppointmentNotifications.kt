package com.utamas.appointments.architecture.notifications

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.utamas.appointments.R
import com.utamas.appointments.activity.ListAppointmentsActivity
import java.lang.ref.WeakReference

class AppointmentNotifications (activity: Activity){
    private val CHANNEL_ID="channel 1"
    private var context: WeakReference<Activity>
    init{
        context=WeakReference(activity)
    }
    private var channelCreated=false
    private fun createNotificationChannel(){
        val context=this.context.get()
        if(!channelCreated&&context!=null){
            channelCreated=true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.channel_name)
                val descriptionText = context.resources.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun showAppointmentNotification(text: String, id: Int){
        createNotificationChannel()
        val context=this.context.get()
        if(context!=null){
            val intent = Intent(context, ListAppointmentsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.outline_announcement_24)
                .setContentTitle(context.resources.getString(R.string.app_name))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(context)) {
                notify(id, builder.build())
            }
        }



    }
}