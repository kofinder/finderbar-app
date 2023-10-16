package com.finderbar.jovian.services

import com.finderbar.jovian.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage;
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.finderbar.jovian.activity.DiscussActivity
import com.finderbar.jovian.prefs
import me.leolin.shortcutbadger.ShortcutBadger
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class FCMMessageService: FirebaseMessagingService() {

    private val channelId = "finderbar-dmin"
    private var mNotificationManager: NotificationManager? = null

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val result = remoteMessage.data
        val notifyId = result["notifyId"]
        val fullMessage = result["fullMessage"]
        val avatar = result["imageUrl"]
        prefs.menuBadgeCount = result["badgeCount"].toString()

        val notificationIntent = Intent(this, DiscussActivity::class.java)
        notificationIntent.putExtra("discussId", notifyId);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val pendingIntent = PendingIntent.getActivity(this, 0 , notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        val notificationId = Random.nextInt(0, 1000)

        val bitmap = avatar?.let { getBitMapUrl(it) }

        val likeIntent = Intent(this, DiscussActivity::class.java)
        likeIntent.putExtra("discussId", notifyId);
        val likePendingIntent = PendingIntent.getService(this, notificationId+1,likeIntent,PendingIntent.FLAG_ONE_SHOT)

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager!!.cancel(notificationId)

        var count: Int = prefs.badgeCount.toInt();
        val badgeCount = count.plus(1)
        prefs.badgeCount = badgeCount.toString();
        val builder = NotificationCompat.Builder(this, channelId)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setStyle(NotificationCompat.BigPictureStyle().setSummaryText(fullMessage).bigPicture(bitmap))
                .addAction(R.drawable.ic_favorite_outline, getString(R.string.notification_add_to_cart_button),likePendingIntent)
                .setContentIntent(pendingIntent)
                .setNumber(badgeCount)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannel()
            builder.setChannelId(channelId)
        }

        val notification = builder.build()
        ShortcutBadger.applyNotification(applicationContext, notification, badgeCount)
        ShortcutBadger.applyCount(applicationContext, badgeCount); //for 1.1.3

        mNotificationManager!!.notify(notificationId, notification)

    }


     private fun getBitMapUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl);
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true;
            connection.connect();
            val input = connection.inputStream;
             return BitmapFactory.decodeStream(input);
        } catch (e: Exception) {
            e.printStackTrace();
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupNotificationChannel() {
        val channelName = getString(R.string.notifications_channel_name);
        val channelDescription = getString(R.string.notifications_channel_description);
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        with(channel) {
            channel.description = channelDescription;
            enableLights(true);
            channel.lightColor = Color.RED;
            enableVibration(true)
            channel.setShowBadge(true)
        };

        mNotificationManager!!.createNotificationChannel(channel)
    }

}