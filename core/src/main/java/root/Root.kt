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

    // Native methods
    external fun getApiKey(): String
    external fun setApiKey(v: String)
    external fun getClientId(): String
    external fun setClientId(v: String)
    external fun getLanguage(): String
    external fun setLanguage(v: String)
    external fun getDevice(): DeviceParams
    external fun setDevice(v: DeviceParams)
    external fun getAppVersion(): String
    external fun setAppVersion(v: String)
    external fun getSdkVersion(): String
    external fun setSdkVersion(v: String)
    external fun getTimezone(): String
    external fun setTimezone(v: String)

    // Kotlin property wrappers for convenient access
    var apiKey: String
        get() = getApiKey()
        set(v) = setApiKey(v)
    var clientId: String
        get() = getClientId()
        set(v) = setClientId(v)
    var language: String
        get() = getLanguage()
        set(v) = setLanguage(v)
    var device: DeviceParams
        get() = getDevice()
        set(v) = setDevice(v)
    var appVersion: String
        get() = getAppVersion()
        set(v) = setAppVersion(v)
    var sdkVersion: String
        get() = getSdkVersion()
        set(v) = setSdkVersion(v)
    var timezone: String
        get() = getTimezone()
        set(v) = setTimezone(v)

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
    external fun getOs(): String
    external fun setOs(v: String)
    external fun getOsVersion(): String
    external fun setOsVersion(v: String)
    external fun getManufacturer(): String
    external fun setManufacturer(v: String)
    external fun getModel(): String
    external fun setModel(v: String)

    // Kotlin property wrappers
    var os: String
        get() = getOs()
        set(v) = setOs(v)
    var osVersion: String
        get() = getOsVersion()
        set(v) = setOsVersion(v)
    var manufacturer: String
        get() = getManufacturer()
        set(v) = setManufacturer(v)
    var model: String
        get() = getModel()
        set(v) = setModel(v)

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
    external fun getApiKey(): String
    external fun setApiKey(v: String)
    external fun getClientId(): String
    external fun setClientId(v: String)
    external fun getLanguage(): String
    external fun setLanguage(v: String)
    external fun getOs(): String
    external fun setOs(v: String)
    external fun getAppVersion(): String
    external fun setAppVersion(v: String)
    external fun getUtcSent(): String
    external fun setUtcSent(v: String)
    external fun getFeedbackType(): String
    external fun setFeedbackType(v: String)
    external fun getDescription(): String
    external fun setDescription(v: String)
    external fun getErrorCode(): String
    external fun setErrorCode(v: String)
    external fun getErrorMessage(): String
    external fun setErrorMessage(v: String)
    external fun getDsHostName(): String
    external fun setDsHostName(v: String)
    external fun getEmail(): String
    external fun setEmail(v: String)

    // Kotlin property wrappers
    var apiKey: String
        get() = getApiKey()
        set(v) = setApiKey(v)
    var clientId: String
        get() = getClientId()
        set(v) = setClientId(v)
    var language: String
        get() = getLanguage()
        set(v) = setLanguage(v)
    var os: String
        get() = getOs()
        set(v) = setOs(v)
    var appVersion: String
        get() = getAppVersion()
        set(v) = setAppVersion(v)
    var utcSent: String
        get() = getUtcSent()
        set(v) = setUtcSent(v)
    var feedbackType: String
        get() = getFeedbackType()
        set(v) = setFeedbackType(v)
    var description: String
        get() = getDescription()
        set(v) = setDescription(v)
    var errorCode: String
        get() = getErrorCode()
        set(v) = setErrorCode(v)
    var errorMessage: String
        get() = getErrorMessage()
        set(v) = setErrorMessage(v)
    var dsHostName: String
        get() = getDsHostName()
        set(v) = setDsHostName(v)
    var email: String
        get() = getEmail()
        set(v) = setEmail(v)

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
    external fun getApiKey(): String
    external fun setApiKey(v: String)
    external fun getClientId(): String
    external fun setClientId(v: String)

    // Kotlin property wrappers
    var apiKey: String
        get() = getApiKey()
        set(v) = setApiKey(v)
    var clientId: String
        get() = getClientId()
        set(v) = setClientId(v)

    companion object {
        @JvmStatic private external fun __New(): Int

        init { go.Seq.touch() }
    }
}
