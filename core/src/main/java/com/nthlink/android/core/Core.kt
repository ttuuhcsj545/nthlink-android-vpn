package com.nthlink.android.core

import com.nthlink.android.core.utils.EMPTY
import root.ConfigParams
import root.DeviceParams
import root.Root

object Core {
    
    // nthLink v7.0.0 API 密钥（从 App.smali 提取）
    private const val API_KEY = "dYUpjec4MFBweMmFWen/t7Ek5mOvV1oB/shHpnh1VKAw4j/aEoWDismJOxBzEknLzEY/cfcbLrKQ4dp1sUsjC04JZn7xEpo076kxc1BWqYnyoD7SEDhoenUrGg84xG5tBTeYhHGBrLS8MJhyMwNMlD/AjA+iBLV+Fvt/zF3oMsIuV1R6EH8IbnFQrlkOFHEcXyFrFi+un5QubIVgm2WnfmWRWCWOLIM+MqNQV2GYMZgSmNEKy/XbtetIUsAr73ZQXYORADMlO6gfuaC0pov+UskTmMZHl7yYygB70FA99cUictvL4YM+NM31xtIiNjKjZ0ZnLJy2oJkvYahwifU4EPicCE92Y47I+cpVv5rYKw67EFV21Zv+eKlU++wsjvSOg99baZ95w3QsdvD+9v0WF3IKGyeP/2xKkI677f0Hhsj8at/+YsZIPn0u6i3QVU3eY425jGDY/2MoFcJtukwy40ZTf2LV5qpASFbY3bkL2/3ITV1wKhiqZz5z8cFFqICA7oLteM5M/swpJ6Zd00zvYV7Zo6tqDBc0wD3xIhnfXn0wyOrWrASiVZxW58H0fJoR9VPHCRkIrzXBRacTFbss48aEgzDB30NC8heU4q6vzR+ceLXDGHD4hJAPm+Xf5j/KTrd/MetPgkvZ8PFntskqoRiLvEgSiPrW3lu+7qsjYqfLqCPzU5y5mUID9hHxrh0z5vRY8PfWhuk8LJ477/gX7tDDldT/lEZD22OZJuwtXceQCtcCKrhnOBePJ2omx96vvGFaAXaT4R22SBW8r18lJ915RaBuzElUyrN17OZZasOICtUsd6Zxcx53lMFLvBwU2lQVrvGeBYmr0qRmEx2Ok9vFk47bSaMmh7cxAZ7iuX+1rXE6uMmmrkbdDFsQsn4G02PLff19+CBkT/875bbY+DqhBxDz+jk3UkcjhZheR2PyUIe7Ywg8mtUvW0wknu/RKjFSMaLs8SwcHFN9MVIIQylFWQNKng7lPTqMzUAhkh8N+e/AA94TpDf7UhYYPW1fFawRp619FZoAEZfzhaw2AvVLY8jKoQvVHznzMe9Ch/zE3SdVK/4dT/PORTqHEVJ8JYJ/nFgLtwNlaSL8PdyjU1SqYdmIKMfRg8RkOpsMevYskyXS5PJHfH2a5ekveC8Ca57eR1tkC3IGN/UISqIbv0DPxfgikNkHHt1VhUCWaq74AJTKMwVWKPoz3cTUefPSdILoxOGpuuXsvcjLcsXpTQb+90I0ZvJp0I8SoVmy9tO5f4bmXRVrzDiXN4TuUbGyfymUmTsn7xQfVyRnIlTYI+oEXeQLu0LNp25+m6b8ouQ="
    private const val CONFIG_URL = "https://s3.us-west-1.amazonaws.com/nthassets/getserver3"
    
    private var isInitialized = false
    
    fun init(debug: Boolean = false) {
        if (!isInitialized) {
            Root.init(API_KEY, debug)
            isInitialized = true
        }
    }

    fun encrypt(text: String): String {
        init()
        return Root.encrypt(text)
    }

    fun decrypt(text: String): String {
        init()
        return Root.decrypt(text)
    }

    fun getConfig(
        clientId: String,
        language: String,
        device: String,
        appVersion: String,
        sdkVersion: String,
        timezone: String
    ): String {
        init()
        
        val deviceParams = DeviceParams().apply {
            this.os = "Android"
            this.osVersion = android.os.Build.VERSION.RELEASE
            this.manufacturer = android.os.Build.MANUFACTURER
            this.model = device
        }
        
        val params = ConfigParams().apply {
            this.apiKey = API_KEY
            this.clientId = clientId
            this.language = language
            this.device = deviceParams
            this.appVersion = appVersion
            this.sdkVersion = sdkVersion
            this.timezone = timezone
        }
        
        return Root.getConfig(
            CONFIG_URL,
            params,
            object : root.DoRequestListener {
                override fun doRequest(
                    url: String,
                    data: ByteArray,
                    headers: Map<String, String>
                ): ByteArray {
                    // 使用原生 HTTP 客户端
                    val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json")
                    headers.forEach { (k, v) -> connection.setRequestProperty(k, v) }
                    
                    connection.outputStream.write(data)
                    return connection.inputStream.readBytes()
                }
            },
            object : root.VerifyResponseListener {
                override fun verifyResponse(data: ByteArray): Boolean {
                    return data.isNotEmpty()
                }
            }
        )
    }

    fun getConfig(): String {
        return getConfig(
            clientId = java.util.UUID.randomUUID().toString(),
            language = "zh-CN",
            device = android.os.Build.MODEL,
            appVersion = "7.0.0",
            sdkVersion = "7.0.0",
            timezone = java.util.TimeZone.getDefault().id
        )
    }

    fun feedback(
        feedbackType: String,
        description: String = EMPTY,
        appVersion: String = EMPTY,
        email: String = EMPTY
    ) {
        init()
        val params = root.FeedbackParams().apply {
            this.apiKey = API_KEY
            this.feedbackType = feedbackType
            this.description = description
            this.appVersion = appVersion
            this.email = email
        }
        Root.feedback(API_KEY, params)
    }

    fun startDiagnostics(): String {
        init()
        return "Report ID: ${System.currentTimeMillis()}"
    }
}
