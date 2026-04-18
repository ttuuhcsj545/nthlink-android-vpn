@file:Suppress("unused")
package go

import android.content.Context
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.util.IdentityHashMap
import java.util.logging.Logger

/**
 * go.Seq — gobind JNI runtime support class.
 * Reconstructed from Seq.smali + all inner class smali files.
 * Package MUST be "go" so JNI symbols resolve correctly.
 */
class Seq private constructor() {

    // -------------------------------------------------------
    // Inner interfaces / classes matching smali inner classes
    // -------------------------------------------------------

    /** go/Seq$GoObject — interface with incRefnum() */
    interface GoObject {
        fun incRefnum(): Int
    }

    /** go/Seq$Proxy — extends GoObject, marks gobind proxy objects */
    interface Proxy : GoObject

    /** go/Seq$Ref — tracks a Java object reference with a refnum */
    class Ref(val refnum: Int, val obj: Any?) {
        private var refcnt: Int = 0

        init {
            require(refnum >= 0) { "Ref instantiated with a Go refnum $refnum" }
        }

        @Synchronized
        fun inc() {
            if (refcnt == Int.MAX_VALUE) {
                throw RuntimeException("refnum $refnum overflow")
            }
            refcnt++
        }

        internal fun decAndGet(): Int {
            return --refcnt
        }

        internal fun getRefcnt(): Int = refcnt
    }

    /** go/Seq$RefMap — sparse int-keyed map backed by array */
    class RefMap {
        private val map = HashMap<Int, Ref>()

        @Synchronized fun get(refnum: Int): Ref? = map[refnum]
        @Synchronized fun put(refnum: Int, ref: Ref) { map[refnum] = ref }
        @Synchronized fun remove(refnum: Int) { map.remove(refnum) }
    }

    /** go/Seq$RefTracker — manages Java-side object lifetime */
    class RefTracker {
        private val javaObjs = RefMap()
        private val javaRefs = IdentityHashMap<Any, Int>()
        private var next: Int = REF_OFFSET

        @Synchronized
        fun inc(obj: Any?): Int {
            if (obj == null) return NULL_REFNUM
            if (obj is Proxy) return obj.incRefnum()

            var refnum = javaRefs[obj]
            if (refnum == null) {
                if (next == Int.MAX_VALUE) {
                    throw RuntimeException("createRef overflow for $obj")
                }
                refnum = next++
                javaRefs[obj] = refnum
            }
            var ref = javaObjs.get(refnum)
            if (ref == null) {
                ref = Ref(refnum, obj)
                javaObjs.put(refnum, ref)
            }
            ref.inc()
            return refnum
        }

        @Synchronized
        fun dec(refnum: Int) {
            if (refnum <= 0) {
                logger.severe("dec request for Go object $refnum")
                return
            }
            if (refnum == nullRef.refnum) return
            val ref = javaObjs.get(refnum) ?: throw RuntimeException("referenced Java object is not found: refnum=$refnum")
            if (ref.decAndGet() <= 0) {
                javaObjs.remove(refnum)
                javaRefs.remove(ref.obj)
            }
        }

        @Synchronized
        fun get(refnum: Int): Ref {
            if (refnum < 0) throw RuntimeException("ref called with Go refnum $refnum")
            if (refnum == NULL_REFNUM) return nullRef
            return javaObjs.get(refnum) ?: throw RuntimeException("unknown java Ref: $refnum")
        }

        @Synchronized
        fun incRefnum(refnum: Int) {
            val ref = javaObjs.get(refnum) ?: throw RuntimeException("referenced Java object is not found: refnum=$refnum")
            ref.inc()
        }

        companion object {
            private const val REF_OFFSET = 42
        }
    }

    /** go/Seq$GoRef — phantom reference for Go-side objects */
    class GoRef(obj: GoObject, queue: ReferenceQueue<GoObject>, val refnum: Int)
        : PhantomReference<GoObject>(obj, queue)

    /** go/Seq$GoRefQueue — tracks Go-side object lifecycles */
    class GoRefQueue : ReferenceQueue<GoObject>() {
        fun track(refnum: Int, obj: GoObject) {
            GoRef(obj, this, refnum)
        }
    }

    companion object {
        private const val NULL_REFNUM = 0x29  // 41

        private val logger: Logger = Logger.getLogger("GoSeq")
        @JvmField val nullRef: Ref = Ref(NULL_REFNUM, null)
        private val goRefQueue = GoRefQueue()
        @JvmField val tracker = RefTracker()

        init {
            System.loadLibrary("gojni")
            init()
            Universe.touch()
        }

        /** Called by Root.<clinit> to trigger Seq class loading */
        @JvmStatic fun touch() { /* triggers class init */ }

        @JvmStatic fun decRef(refnum: Int) { tracker.dec(refnum) }
        @JvmStatic fun getRef(refnum: Int): Ref = tracker.get(refnum)
        @JvmStatic fun incRef(obj: Any): Int = tracker.inc(obj)
        @JvmStatic fun incRefnum(refnum: Int) { tracker.incRefnum(refnum) }
        @JvmStatic fun incGoObjectRef(obj: GoObject): Int = obj.incRefnum()

        /**
         * Track a Go-side object reference.
         * refnum < 0 means Go-allocated (negative refnum = Go refnum)
         */
        @JvmStatic fun trackGoRef(refnum: Int, obj: GoObject) {
            if (refnum > 0) {
                // positive means Java refnum — shouldn't be passed here
                logger.severe("trackGoRef called with Java refnum $refnum")
                return
            }
            goRefQueue.track(refnum, obj)
        }

        // Android-typed overload for setContext
        @JvmStatic fun setContext(ctx: Context) { setContext(ctx as Any) }

        // Native methods
        @JvmStatic private external fun init()
        @JvmStatic external fun destroyRef(refnum: Int)
        @JvmStatic external fun incGoRef(refnum: Int, obj: GoObject)
        @JvmStatic external fun setContext(ctx: Any)
    }
}
