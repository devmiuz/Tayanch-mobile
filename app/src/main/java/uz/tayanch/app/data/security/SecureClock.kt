package uz.tayanch.app.data.security

import android.os.SystemClock

/**
 * Pillar 4 — Anti-time-tampering. Quiz/battle timers measure elapsed time with
 * [SystemClock.elapsedRealtime] (monotonic milliseconds since boot) instead of
 * System.currentTimeMillis(). The user controls the wall clock; they do NOT
 * control elapsedRealtime, so spoofing the timezone/date can't buy extra time.
 * The server independently re-checks its own UTC delta.
 */
object SecureClock {
    fun nowMonotonic(): Long = SystemClock.elapsedRealtime()
}
