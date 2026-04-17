package com.leaf

import android.net.VpnService
import android.os.ParcelFileDescriptor

class LeafService : VpnService() {
    
    var vpnInterface: ParcelFileDescriptor? = null
    
    override fun onCreate() {
        super.onCreate()
        LeafJni.leafSetProtectSocketCallback(this, "protectSocket")
    }
    
    fun startVPN(config: String): Int {
        vpnInterface = Builder()
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .addDnsServer("8.8.4.4")
            .establish()
        
        return LeafJni.leafStart(config)
    }
    
    fun stopVPN() {
        LeafJni.leafStop()
        vpnInterface?.close()
        vpnInterface = null
    }
    
    fun protectSocket(fd: Int): Boolean {
        return protect(fd)
    }
}
