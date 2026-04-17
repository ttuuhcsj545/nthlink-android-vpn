package com.nthlink.android.core

import android.content.Context
import com.leaf.LeafService
import com.nthlink.android.core.model.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

internal class RootVpnClient(context: Context) : RootVpn(context) {
    
    private var leafService: LeafService? = null
    
    override suspend fun runVpn(servers: List<Config.Server>) {
        val config = buildLeafConfig(servers)
        runVpn(config)
    }

    override suspend fun runVpn(config: String) {
        withContext(Dispatchers.IO) {
            updateStatus(Root.Status.CONNECTING)
            
            try {
                leafService = LeafService()
                val result = leafService?.startVPN(config)
                
                if (result == 0) {
                    updateStatus(Root.Status.CONNECTED)
                } else {
                    updateStatus(Root.Status.DISCONNECTED)
                }
            } catch (e: Exception) {
                updateStatus(Root.Status.DISCONNECTED)
            }
        }
    }

    override fun disconnect() {
        updateStatus(Root.Status.DISCONNECTING)
        leafService?.stopVPN()
        leafService = null
        updateStatus(Root.Status.DISCONNECTED)
    }
    
    private fun buildLeafConfig(servers: List<Config.Server>): String {
        val outbounds = JSONArray()
        
        servers.forEach { server ->
            val outbound = JSONObject().apply {
                put("protocol", "shadowsocks")
                put("tag", "proxy-${server.host}")
                
                val settings = JSONObject().apply {
                    put("address", server.host)
                    put("port", server.port)
                    put("method", server.encryptMethod)
                    put("password", server.password)
                }
                put("settings", settings)
                
                // WebSocket 传输层
                if (server.ws) {
                    val streamSettings = JSONObject().apply {
                        put("transport", "ws")
                        val wsSettings = JSONObject().apply {
                            put("path", server.wsPath)
                            val headers = JSONObject().apply {
                                put("Host", server.wsHost)
                            }
                            put("headers", headers)
                        }
                        put("transportSettings", wsSettings)
                        
                        // TLS 设置
                        if (server.sni.isNotEmpty()) {
                            val tlsSettings = JSONObject().apply {
                                put("serverName", server.sni)
                            }
                            put("security", "tls")
                            put("tlsSettings", tlsSettings)
                        }
                    }
                    put("streamSettings", streamSettings)
                }
            }
            outbounds.put(outbound)
        }
        
        // 添加 direct 出站
        outbounds.put(JSONObject().apply {
            put("protocol", "direct")
            put("tag", "direct")
        })
        
        val configJson = JSONObject().apply {
            // 入站：TUN 设备
            val inbounds = JSONArray().apply {
                put(JSONObject().apply {
                    put("protocol", "tun")
                    put("tag", "tun-in")
                    val tunSettings = JSONObject().apply {
                        put("name", "tun0")
                        put("mtu", 1500)
                        put("inet4_address", "10.0.0.2/30")
                        put("inet4_gateway", "10.0.0.1")
                    }
                    put("settings", tunSettings)
                })
            }
            put("inbounds", inbounds)
            
            // 出站
            put("outbounds", outbounds)
            
            // 路由规则
            val rules = JSONArray().apply {
                put(JSONObject().apply {
                    put("ip", JSONArray().apply { put("geoip:private") })
                    put("outbound", "direct")
                })
            }
            put("rules", rules)
            
            // DNS
            val dns = JSONObject().apply {
                val servers = JSONArray().apply {
                    put(JSONObject().apply {
                        put("tag", "google-dns")
                        put("address", "8.8.8.8")
                        put("address_resolver", "local-dns")
                    })
                    put(JSONObject().apply {
                        put("tag", "local-dns")
                        put("address", "223.5.5.5")
                        put("address_resolver", "local-dns")
                    })
                }
                put("servers", servers)
                put("rules", JSONArray().apply {
                    put(JSONObject().apply {
                        put("geosite", "cn")
                        put("server", "local-dns")
                    })
                })
            }
            put("dns", dns)
            
            // 日志
            put("log", JSONObject().apply {
                put("level", "info")
            })
        }
        
        return configJson.toString()
    }
}
