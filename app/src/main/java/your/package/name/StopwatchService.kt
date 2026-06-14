package your.package.name

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class StopwatchService : Service() {

    private val CHANNEL_ID = "StopwatchChannel"
    private var handler = Handler(Looper.getMainLooper())
    private var minutes: Int = 0
    private var isRunning: Boolean = false
    private var isPaused: Boolean = false

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_CLOSE = "ACTION_CLOSE"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (!isRunning) {
                    isRunning = true
                    isPaused = false
                    startForeground(1, buildNotification())
                    updateStopwatch()
                } else if (isPaused) {
                    isPaused = false
                    updateStopwatch()
                }
            }
            ACTION_PAUSE -> {
                if (isRunning && !isPaused) {
                    isPaused = true
                    handler.removeCallbacks(updateRunnable)
                    updateNotification()
                }
            }
            ACTION_STOP -> {
                isRunning = false
                isPaused = false
                handler.removeCallbacks(updateRunnable)
                stopForeground(true)
                stopSelf()
            }
            ACTION_CLOSE -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val name = "Stopwatch Notifications"
        val descriptionText = "Channel for stopwatch notifications"
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning && !isPaused) {
                minutes++
                updateNotification()
                handler.postDelayed(this, 60000)
            }
        }
    }

    private fun updateStopwatch() {
        handler.post(updateRunnable)
    }

    private fun updateNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, buildNotification())
    }

    private fun buildNotification(): Notification {
        val startPauseIntent = Intent(this, StopwatchService::class.java).apply {
            action = if (isPaused) ACTION_START else ACTION_PAUSE
        }
        val stopIntent = Intent(this, StopwatchService::class.java).apply {
            action = ACTION_STOP
        }
        val closeIntent = Intent(this, StopwatchService::class.java).apply {
            action = ACTION_CLOSE
        }

        val startPausePendingIntent = PendingIntent.getService(
            this, 0, startPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val closePendingIntent = PendingIntent.getService(
            this, 2, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val remoteViews = RemoteViews(packageName, R.layout.notification_stopwatch)

        val timeString = String.format("%02d", minutes)
        remoteViews.setTextViewText(R.id.tvTime, timeString)
        remoteViews.setTextViewText(R.id.btnStartPause, if (isPaused) "Resume" else "Start")

        remoteViews.setOnClickPendingIntent(R.id.btnStartPause, startPausePendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.btnStop, stopPendingIntent)
        remoteViews.setOnClickPendingIntent(R.id.btnCloseNotification, closePendingIntent)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContent(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
}
