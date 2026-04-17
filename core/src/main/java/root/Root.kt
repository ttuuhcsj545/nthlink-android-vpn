package root

object Root {
    
    init {
        System.loadLibrary("gojni")
    }
    
    @JvmStatic
    external fun init(apiKey: String, debug: Boolean)
    
    @JvmStatic
    external fun getConfig(
        url: String,
        params: ConfigParams,
        doRequestListener: DoRequestListener,
        verifyResponseListener: VerifyResponseListener
    ): String
    
    @JvmStatic
    external fun getConfigWithJson(
        url: String,
        jsonParams: String,
        doRequestListener: DoRequestListener,
        verifyResponseListener: VerifyResponseListener
    ): String
    
    @JvmStatic
    external fun encrypt(data: String): String
    
    @JvmStatic
    external fun decrypt(data: String): String
    
    @JvmStatic
    external fun feedback(apiKey: String, params: FeedbackParams): String
    
    @JvmStatic
    external fun report(apiKey: String, params: ReportParams): String
    
    @JvmStatic
    external fun startDiagnostics(
        apiKey: String,
        params: ConfigParams,
        proxyHost: String,
        proxyPort: String,
        proxyPassword: String,
        vpn: VPN
    ): String
}

interface DoRequestListener {
    fun doRequest(url: String, data: ByteArray, headers: Map<String, String>): ByteArray
}

interface VerifyResponseListener {
    fun verifyResponse(data: ByteArray): Boolean
}

interface VPN {
    fun disconnect()
    fun isConnected(tag: String): Boolean
}

class ConfigParams {
    var apiKey: String = ""
    var clientId: String = ""
    var language: String = ""
    var device: String = ""
    var appVersion: String = ""
    var sdkVersion: String = ""
    var timezone: String = ""
}

class FeedbackParams {
    var apiKey: String = ""
    var feedbackType: String = ""
    var description: String = ""
    var appVersion: String = ""
    var email: String = ""
}

class ReportParams {
    var apiKey: String = ""
    var clientId: String = ""
    var event: String = ""
    var error: String = ""
    var info: String = ""
}
