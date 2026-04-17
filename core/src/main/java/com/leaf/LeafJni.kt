package com.leaf

object LeafJni {
    
    init {
        System.loadLibrary("leafandroid")
    }
    
    @JvmStatic
    external fun leafStart(config: String): Int
    
    @JvmStatic
    external fun leafStop()
    
    @JvmStatic
    external fun healthCheck(tag: String, timeout: Long): Int
    
    @JvmStatic
    external fun getLastActive(): Long
    
    @JvmStatic
    external fun getSinceLastActive(): Long
    
    @JvmStatic
    external fun leafSetProtectSocketCallback(callback: Any, methodName: String)
}
