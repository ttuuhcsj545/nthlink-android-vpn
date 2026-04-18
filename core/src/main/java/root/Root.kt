package root

// ============================================================
// go/Seq — gobind runtime support class
// Must be in package "go" to match JNI symbol: go/Seq
// Reconstructed from Seq.smali; loadLibrary + native stubs
// ============================================================
object Seq {
    init {
        System.loadLibrary("gojni")
    }

    // Called by Root.<clinit> to ensure this class (and gojni) is loaded first
    @JvmStatic
    fun touch() { /* triggers class init */ }

    @JvmStatic
    external fun destroyRef(refnum: Int)

    @JvmStatic
    fun decRef(refnum: Int) { /* no-op stub; tracker handled by native */ }

    @JvmStatic
    external fun incGoRef(refnum: Int, obj: Any)

    @JvmStatic
    fun incRef(obj: Any): Int = 0   // stub; native side manages

    @JvmStatic
    fun incRefnum(refnum: Int) { /* stub */ }

    @JvmStatic
    external fun setContext(ctx: Any)
}

// ============================================================
// root/Root — gobind-generated JNI bridge
// Must match Root.smali exactly: abstract class, static clinit
// calling Seq.touch() then _init()
// ============================================================
abstract class Root {
    companion object {
        const val Version: String = "3.3.7"

        init {
            Seq.touch()   // ensures gojni is loaded
            _init()       // gobind registration
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
        external fun feedbackWithJson(apiKey: String, jsonParams: String): String

        @JvmStatic
        external fun report(apiKey: String, params: ReportParams): String

        @JvmStatic
        external fun reportWithJson(apiKey: String, jsonParams: String): String

        @JvmStatic
        external fun startDiagnostics(
            apiKey: String,
            params: ConfigParams,
            proxyHost: String,
            proxyPort: String,
            proxyPassword: String,
            vpn: VPN
        ): String

        /** No-op — triggers companion object (and thus clinit) */
        @JvmStatic
        fun touch() { /* triggers class init */ }

        @JvmStatic
        private external fun _init()
    }
}

// ============================================================
// Interfaces & data classes (match root/*.smali exactly)
// ============================================================

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

class DeviceParams {
    var apiKey: String = ""
    var clientId: String = ""
    var language: String = ""
    var device: String = ""
    var appVersion: String = ""
    var sdkVersion: String = ""
    var timezone: String = ""
}
