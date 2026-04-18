package com.leaf

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log

/**
 * LeafService — 正确的 VpnService 实现
 *
 * 使用方式：通过 Intent + bindService/startService 启动，
 * 而不是直接实例化。
 *
 * 控制流：
 *   startService(Intent(ACTION_START).putExtra(EXTRA_CONFIG, configJson))
 *   startService(Intent(ACTION_STOP))
 */
class LeafService : VpnService() {

    companion object {
        const val ACTION_START = "com.leaf.LeafService.START"
        const val ACTION_STOP  = "com.leaf.LeafService.STOP"
        const val EXTRA_CONFIG = "config"

        private const val TAG = "LeafService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "leaf_vpn"

        /** 启动 VPN（Context 可以是 Activity / Fragment） */
        fun start(context: Context, config: String) {
            val intent = Intent(context, LeafService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_CONFIG, config)
            }
            context.startService(intent)
        }

        /** 停止 VPN */
        fun stop(context: Context) {
            val intent = Intent(context, LeafService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private val binder = LocalBinder()
    private var isRunning = false

    inner class LocalBinder : Binder() {
        fun getService(): LeafService = this@LeafService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        // 注册 socket 保护回调，使 VPN 流量不被自身捕获
        LeafJni.leafSetProtectSocketCallback(this, "protect")
        Log.d(TAG, "LeafService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val config = intent.getStringExtra(EXTRA_CONFIG) ?: return START_NOT_STICKY
                startVpn(config)
            }
            ACTION_STOP -> {
                stopVpn()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startVpn(config: String) {
        if (isRunning) {
            Log.w(TAG, "VPN already running, stopping first")
            LeafJni.leafStop()
        }

        // 建立 TUN 接口
        val tun = Builder()
            .setSession("nthLink")
            .addAddress("10.0.0.2", 30)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .addDnsServer("8.8.4.4")
            .setMtu(1500)
            .establish()

        if (tun == null) {
            Log.e(TAG, "Failed to establish TUN interface")
            return
        }

        // 启动前台通知（Android 8+ 必须）
        startForeground(NOTIFICATION_ID, buildNotification())

        // 启动 Leaf
        val ret = LeafJni.leafStart(config)
        isRunning = (ret == 0)
        Log.d(TAG, "leafStart returned $ret, isRunning=$isRunning")
    }

    private fun stopVpn() {
        if (isRunning) {
            LeafJni.leafStop()
            isRunning = false
        }
        stopForeground(true)
        Log.d(TAG, "VPN stopped")
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    /** 被 LeafJni 通过 reflect 调用，保护套接字不走 VPN */
    @Suppress("unused")
    fun protect(fd: Int): Boolean = protect(fd)

    // ----------------------------------------------------------
    // 前台通知
    // ----------------------------------------------------------
    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VPN Service",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, LeafService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("nthLink VPN")
            .setContentText("VPN is running")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .addAction(Notification.Action.Builder(null, "Stop", stopPending).build())
            .build()
    }
}
