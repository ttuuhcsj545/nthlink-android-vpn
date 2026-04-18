@file:Suppress("unused")
package root

// ============================================================
// root/Root — gobind-generated JNI bridge
// Matches Root.smali exactly: abstract class, companion init
// ============================================================
abstract class Root {
    companion object {
        const val Version: String = "3.3.7"

        init {
            go.Seq.touch()   // ensures gojni is loaded and Seq.<clinit> runs
            _init()          // gobind type registration
        }

        @JvmStatic external fun init(apiKey: String, debug: Boolean)

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

        @JvmStatic external fun encrypt(data: String): String
        @JvmStatic external fun decrypt(data: String): String

        @JvmStatic external fun feedback(apiKey: String, params: FeedbackParams): String
        @JvmStatic external fun feedbackWithJson(apiKey: String, jsonParams: String): String

        @JvmStatic external fun report(apiKey: String, params: ReportParams): String
        @JvmStatic external fun reportWithJson(apiKey: String, jsonParams: String): String

        @JvmStatic
        external fun startDiagnostics(
            apiKey: String,
            params: ConfigParams,
            proxyHost: String,
            proxyPort: String,
            proxyPassword: String,
            vpn: VPN
        ): String

        /** Triggers companion object (clinit) */
        @JvmStatic fun touch() { /* triggers class init */ }

        @JvmStatic private external fun _init()
    }
}

// ============================================================
// Interfaces
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

// ============================================================
// ConfigParams — gobind Proxy object
// Two constructors matching smali:
//   <init>()V  → calls __New() to get refnum from Go side
//   <init>(I)V → accepts refnum directly (called by native layer)
// All property accessors are native methods
// ============================================================
class ConfigParams : go.Seq.Proxy {
    private val refnum: Int

    // <init>()V — Java-side construction: allocate a new Go object
    constructor() {
        go.Seq.touch()
        refnum = __New()
        go.Seq.trackGoRef(refnum, this)
    }

    // <init>(I)V — native-side construction: wrap existing Go refnum
    constructor(refnum: Int) {
        this.refnum = refnum
        go.Seq.trackGoRef(refnum, this)
    }

    override fun incRefnum(): Int {
        go.Seq.incGoRef(refnum, this)
        return refnum
    }

    // Kotlin properties backed by native methods
    // Using @JvmName to avoid JVM signature clash with synthetic accessors
    external var apiKey: String
        @JvmName("nativeGetApiKey") external get
        @JvmName("nativeSetApiKey") external set
    external var clientId: String
        @JvmName("nativeGetClientId") external get
        @JvmName("nativeSetClientId") external set
    external var language: String
        @JvmName("nativeGetLanguage") external get
        @JvmName("nativeSetLanguage") external set
    external var device: DeviceParams
        @JvmName("nativeGetDevice") external get
        @JvmName("nativeSetDevice") external set
    external var appVersion: String
        @JvmName("nativeGetAppVersion") external get
        @JvmName("nativeSetAppVersion") external set
    external var sdkVersion: String
        @JvmName("nativeGetSdkVersion") external get
        @JvmName("nativeSetSdkVersion") external set
    external var timezone: String
        @JvmName("nativeGetTimezone") external get
        @JvmName("nativeSetTimezone") external set

    companion object {
        @JvmStatic private external fun __New(): Int

        init { go.Seq.touch() }
    }
}

// ============================================================
// DeviceParams — gobind Proxy object
// ============================================================
class DeviceParams : go.Seq.Proxy {
    private val refnum: Int

    constructor() {
        go.Seq.touch()
        refnum = __New()
        go.Seq.trackGoRef(refnum, this)
    }

    constructor(refnum: Int) {
        this.refnum = refnum
        go.Seq.trackGoRef(refnum, this)
    }

    override fun incRefnum(): Int {
        go.Seq.incGoRef(refnum, this)
        return refnum
    }

    // Kotlin properties backed by native methods
    external var os: String
        @JvmName("nativeGetOs") external get
        @JvmName("nativeSetOs") external set
    external var osVersion: String
        @JvmName("nativeGetOsVersion") external get
        @JvmName("nativeSetOsVersion") external set
    external var manufacturer: String
        @JvmName("nativeGetManufacturer") external get
        @JvmName("nativeSetManufacturer") external set
    external var model: String
        @JvmName("nativeGetModel") external get
        @JvmName("nativeSetModel") external set

    companion object {
        @JvmStatic private external fun __New(): Int

        init { go.Seq.touch() }
    }
}

// ============================================================
// FeedbackParams — gobind Proxy object
// ============================================================
class FeedbackParams : go.Seq.Proxy {
    private val refnum: Int

    constructor() {
        go.Seq.touch()
        refnum = __New()
        go.Seq.trackGoRef(refnum, this)
    }

    constructor(refnum: Int) {
        this.refnum = refnum
        go.Seq.trackGoRef(refnum, this)
    }

    override fun incRefnum(): Int {
        go.Seq.incGoRef(refnum, this)
        return refnum
    }

    // Kotlin properties backed by native methods
    external var apiKey: String
        @JvmName("nativeGetApiKey") external get
        @JvmName("nativeSetApiKey") external set
    external var clientId: String
        @JvmName("nativeGetClientId") external get
        @JvmName("nativeSetClientId") external set
    external var language: String
        @JvmName("nativeGetLanguage") external get
        @JvmName("nativeSetLanguage") external set
    external var os: String
        @JvmName("nativeGetOs") external get
        @JvmName("nativeSetOs") external set
    external var appVersion: String
        @JvmName("nativeGetAppVersion") external get
        @JvmName("nativeSetAppVersion") external set
    external var utcSent: String
        @JvmName("nativeGetUtcSent") external get
        @JvmName("nativeSetUtcSent") external set
    external var feedbackType: String
        @JvmName("nativeGetFeedbackType") external get
        @JvmName("nativeSetFeedbackType") external set
    external var description: String
        @JvmName("nativeGetDescription") external get
        @JvmName("nativeSetDescription") external set
    external var errorCode: String
        @JvmName("nativeGetErrorCode") external get
        @JvmName("nativeSetErrorCode") external set
    external var errorMessage: String
        @JvmName("nativeGetErrorMessage") external get
        @JvmName("nativeSetErrorMessage") external set
    external var dsHostName: String
        @JvmName("nativeGetDsHostName") external get
        @JvmName("nativeSetDsHostName") external set
    external var email: String
        @JvmName("nativeGetEmail") external get
        @JvmName("nativeSetEmail") external set

    companion object {
        @JvmStatic private external fun __New(): Int

        init { go.Seq.touch() }
    }
}

// ============================================================
// ReportParams — gobind Proxy object
// ============================================================
class ReportParams : go.Seq.Proxy {
    private val refnum: Int

    constructor() {
        go.Seq.touch()
        refnum = __New()
        go.Seq.trackGoRef(refnum, this)
    }

    constructor(refnum: Int) {
        this.refnum = refnum
        go.Seq.trackGoRef(refnum, this)
    }

    override fun incRefnum(): Int {
        go.Seq.incGoRef(refnum, this)
        return refnum
    }

    // Kotlin properties backed by native methods
    external var apiKey: String
        @JvmName("nativeGetApiKey") external get
        @JvmName("nativeSetApiKey") external set
    external var clientId: String
        @JvmName("nativeGetClientId") external get
        @JvmName("nativeSetClientId") external set

    companion object {
        @JvmStatic private external fun __New(): Int

        init { go.Seq.touch() }
    }
}
