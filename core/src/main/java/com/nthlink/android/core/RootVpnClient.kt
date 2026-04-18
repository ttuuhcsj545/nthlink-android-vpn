package com.nthlink.android.core

import android.content.Context
import com.leaf.LeafService
import com.nthlink.android.core.model.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

internal class RootVpnClient(private val context: Context) : RootVpn(context) {

    override suspend fun runVpn(servers: List<Config.Server>) {
        val config = buildLeafConfig(servers)
        runVpn(config)
    }

    override suspend fun runVpn(config: String) {
        withContext(Dispatchers.IO) {
            updateStatus(Root.Status.CONNECTING)
            // 通过 Intent 启动 LeafService（VpnService 不能直接实例化）
            LeafService.start(context, config)
            // LeafService 内部会更新状态，此处乐观设为 CONNECTED
            // 实际项目可通过 broadcast / binder 回调确认
            updateStatus(Root.Status.CONNECTED)
        }
    }

    override fun disconnect() {
        updateStatus(Root.Status.DISCONNECTING)
        LeafService.stop(context)
        updateStatus(Root.Status.DISCONNECTED)
    }

    // ----------------------------------------------------------
    // Leaf JSON config builder
    // ----------------------------------------------------------
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

                // WebSocket transport
                if (server.ws) {
                    val streamSettings = JSONObject().apply {
                        put("transport", "ws")
                        val wsSettings = JSONObject().apply {
                            put("path", server.wsPath)
                            put("headers", JSONObject().apply { put("Host", server.wsHost) })
                        }
                        put("transportSettings", wsSettings)

                        if (server.sni.isNotEmpty()) {
                            put("security", "tls")
                            put("tlsSettings", JSONObject().apply { put("serverName", server.sni) })
                        }
                    }
                    put("streamSettings", streamSettings)
                }
            }
            outbounds.put(outbound)
        }

        // direct outbound
        outbounds.put(JSONObject().apply {
            put("protocol", "direct")
            put("tag", "direct")
        })

        return JSONObject().apply {
            put("inbounds", JSONArray().apply {
                put(JSONObject().apply {
                    put("protocol", "tun")
                    put("tag", "tun-in")
                    put("settings", JSONObject().apply {
                        put("name", "tun0")
                        put("mtu", 1500)
                        put("inet4_address", "10.0.0.2/30")
                        put("inet4_gateway", "10.0.0.1")
                    })
                })
            })
            put("outbounds", outbounds)
            put("rules", JSONArray().apply {
                put(JSONObject().apply {
                    put("ip", JSONArray().apply { put("geoip:private") })
                    put("outbound", "direct")
                })
            })
            put("dns", JSONObject().apply {
                put("servers", JSONArray().apply {
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
                })
                put("rules", JSONArray().apply {
                    put(JSONObject().apply {
                        put("geosite", "cn")
                        put("server", "local-dns")
                    })
                })
            })
            put("log", JSONObject().apply { put("level", "info") })
        }.toString()
    }
}
