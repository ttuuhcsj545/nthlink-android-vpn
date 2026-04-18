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

    // Native methods (used by property accessors)
    @JvmName("nativeGetApiKey") external fun nativeGetApiKey(): String
    @JvmName("nativeSetApiKey") external fun nativeSetApiKey(v: String)
    @JvmName("nativeGetClientId") external fun nativeGetClientId(): String
    @JvmName("nativeSetClientId") external fun nativeSetClientId(v: String)
    @JvmName("nativeGetLanguage") external fun nativeGetLanguage(): String
    @JvmName("nativeSetLanguage") external fun nativeSetLanguage(v: String)
    @JvmName("nativeGetDevice") external fun nativeGetDevice(): DeviceParams
    @JvmName("nativeSetDevice") external fun nativeSetDevice(v: DeviceParams)
    @JvmName("nativeGetAppVersion") external fun nativeGetAppVersion(): String
    @JvmName("nativeSetAppVersion") external fun nativeSetAppVersion(v: String)
    @JvmName("nativeGetSdkVersion") external fun nativeGetSdkVersion(): String
    @JvmName("nativeSetSdkVersion") external fun nativeSetSdkVersion(v: String)
    @JvmName("nativeGetTimezone") external fun nativeGetTimezone(): String
    @JvmName("nativeSetTimezone") external fun nativeSetTimezone(v: String)

    // Kotlin property wrappers
    var apiKey: String
        get() = nativeGetApiKey()
        set(v) = nativeSetApiKey(v)
    var clientId: String
        get() = nativeGetClientId()
        set(v) = nativeSetClientId(v)
    var language: String
        get() = nativeGetLanguage()
        set(v) = nativeSetLanguage(v)
    var device: DeviceParams
        get() = nativeGetDevice()
        set(v) = nativeSetDevice(v)
    var appVersion: String
        get() = nativeGetAppVersion()
        set(v) = nativeSetAppVersion(v)
    var sdkVersion: String
        get() = nativeGetSdkVersion()
        set(v) = nativeSetSdkVersion(v)
    var timezone: String
        get() = nativeGetTimezone()
        set(v) = nativeSetTimezone(v)

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

    // Native methods
    @JvmName("nativeGetOs") external fun nativeGetOs(): String
    @JvmName("nativeSetOs") external fun nativeSetOs(v: String)
    @JvmName("nativeGetOsVersion") external fun nativeGetOsVersion(): String
    @JvmName("nativeSetOsVersion") external fun nativeSetOsVersion(v: String)
    @JvmName("nativeGetManufacturer") external fun nativeGetManufacturer(): String
    @JvmName("nativeSetManufacturer") external fun nativeSetManufacturer(v: String)
    @JvmName("nativeGetModel") external fun nativeGetModel(): String
    @JvmName("nativeSetModel") external fun nativeSetModel(v: String)

    // Kotlin property wrappers
    var os: String
        get() = nativeGetOs()
        set(v) = nativeSetOs(v)
    var osVersion: String
        get() = nativeGetOsVersion()
        set(v) = nativeSetOsVersion(v)
    var manufacturer: String
        get() = nativeGetManufacturer()
        set(v) = nativeSetManufacturer(v)
    var model: String
        get() = nativeGetModel()
        set(v) = nativeSetModel(v)

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

    // Native methods
    @JvmName("nativeGetApiKey") external fun nativeGetApiKey(): String
    @JvmName("nativeSetApiKey") external fun nativeSetApiKey(v: String)
    @JvmName("nativeGetClientId") external fun nativeGetClientId(): String
    @JvmName("nativeSetClientId") external fun nativeSetClientId(v: String)
    @JvmName("nativeGetLanguage") external fun nativeGetLanguage(): String
    @JvmName("nativeSetLanguage") external fun nativeSetLanguage(v: String)
    @JvmName("nativeGetOs") external fun nativeGetOs(): String
    @JvmName("nativeSetOs") external fun nativeSetOs(v: String)
    @JvmName("nativeGetAppVersion") external fun nativeGetAppVersion(): String
    @JvmName("nativeSetAppVersion") external fun nativeSetAppVersion(v: String)
    @JvmName("nativeGetUtcSent") external fun nativeGetUtcSent(): String
    @JvmName("nativeSetUtcSent") external fun nativeSetUtcSent(v: String)
    @JvmName("nativeGetFeedbackType") external fun nativeGetFeedbackType(): String
    @JvmName("nativeSetFeedbackType") external fun nativeSetFeedbackType(v: String)
    @JvmName("nativeGetDescription") external fun nativeGetDescription(): String
    @JvmName("nativeSetDescription") external fun nativeSetDescription(v: String)
    @JvmName("nativeGetErrorCode") external fun nativeGetErrorCode(): String
    @JvmName("nativeSetErrorCode") external fun nativeSetErrorCode(v: String)
    @JvmName("nativeGetErrorMessage") external fun nativeGetErrorMessage(): String
    @JvmName("nativeSetErrorMessage") external fun nativeSetErrorMessage(v: String)
    @JvmName("nativeGetDsHostName") external fun nativeGetDsHostName(): String
    @JvmName("nativeSetDsHostName") external fun nativeSetDsHostName(v: String)
    @JvmName("nativeGetEmail") external fun nativeGetEmail(): String
    @JvmName("nativeSetEmail") external fun nativeSetEmail(v: String)

    // Kotlin property wrappers
    var apiKey: String
        get() = nativeGetApiKey()
        set(v) = nativeSetApiKey(v)
    var clientId: String
        get() = nativeGetClientId()
        set(v) = nativeSetClientId(v)
    var language: String
        get() = nativeGetLanguage()
        set(v) = nativeSetLanguage(v)
    var os: String
        get() = nativeGetOs()
        set(v) = nativeSetOs(v)
    var appVersion: String
        get() = nativeGetAppVersion()
        set(v) = nativeSetAppVersion(v)
    var utcSent: String
        get() = nativeGetUtcSent()
        set(v) = nativeSetUtcSent(v)
    var feedbackType: String
        get() = nativeGetFeedbackType()
        set(v) = nativeSetFeedbackType(v)
    var description: String
        get() = nativeGetDescription()
        set(v) = nativeSetDescription(v)
    var errorCode: String
        get() = nativeGetErrorCode()
        set(v) = nativeSetErrorCode(v)
    var errorMessage: String
        get() = nativeGetErrorMessage()
        set(v) = nativeSetErrorMessage(v)
    var dsHostName: String
        get() = nativeGetDsHostName()
        set(v) = nativeSetDsHostName(v)
    var email: String
        get() = nativeGetEmail()
        set(v) = nativeSetEmail(v)

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

    // Native methods
    @JvmName("nativeGetApiKey") external fun nativeGetApiKey(): String
    @JvmName("nativeSetApiKey") external fun nativeSetApiKey(v: String)
    @JvmName("nativeGetClientId") external fun nativeGetClientId(): String
    @JvmName("nativeSetClientId") external fun nativeSetClientId(v: String)

    // Kotlin property wrappers
    var apiKey: String
        get() = nativeGetApiKey()
        set(v) = nativeSetApiKey(v)
    var clientId: String
        get() = nativeGetClientId()
        set(v) = nativeSetClientId(v)

    companion object {
        @JvmStatic private external fun __New(): Int

        init { go.Seq.touch() }
    }
}
