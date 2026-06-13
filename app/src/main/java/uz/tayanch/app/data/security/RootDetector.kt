package uz.tayanch.app.data.security

import android.content.Context
import com.scottyab.rootbeer.RootBeer
import uz.tayanch.app.BuildConfig

/**
 * Pillar 29 — Runtime integrity (root detection).
 *
 * Uses RootBeer to flag a compromised device (su/Magisk binaries, dangerous
 * props, test-keys build, writable system paths, root-management apps). On a
 * rooted device the Keystore/TEE guarantees and FLAG_SECURE protections weaken,
 * so [MainActivity] blocks the app behind a warning screen.
 *
 * **Disabled for debug builds** (`BuildConfig.DEBUG`): development happens on
 * emulators and rooted test devices, which RootBeer correctly flags — so the
 * check only runs in release builds, where it actually matters.
 *
 * Note: local root checks are a deterrent, not a guarantee — Magisk Hide/Zygisk
 * can defeat them. For a hard gate, pair this with server-verified Play Integrity.
 */
object RootDetector {

    fun isCompromised(context: Context): Boolean {
        if (BuildConfig.DEBUG) return false
        return RootBeer(context).isRooted
    }
}
