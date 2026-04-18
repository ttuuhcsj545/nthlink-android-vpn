@file:Suppress("unused")
package go

/**
 * go.Universe — gobind generated, triggers universe-level type registration.
 * Reconstructed from Universe.smali.
 * clinit: calls Seq.touch() then _init()
 */
abstract class Universe {
    companion object {
        init {
            Seq.touch()
            _init()
        }

        @JvmStatic fun touch() { /* triggers class init */ }

        @JvmStatic private external fun _init()
    }
}
