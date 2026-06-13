package uz.tayanch.app.data.security

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Pillar 15 — single-session concurrency, client half.
 *
 * The backend keeps exactly one active session per user; logging in elsewhere
 * rotates `active_sid` and every token minted for this device starts returning
 * 401 "Sessiya boshqa qurilmada ochilgan". When the refresh attempt also fails
 * (the refresh token is bound to the dead session too), the network layer clears
 * the local session and emits [evictions]; the UI collects it and bounces the
 * user back to the auth gateway with an explanation.
 */
class SessionManager(private val store: SecureSessionStore) {

    private val _evictions = MutableSharedFlow<Reason>(extraBufferCapacity = 1)
    val evictions: SharedFlow<Reason> = _evictions.asSharedFlow()

    enum class Reason { SESSION_REPLACED, REFRESH_FAILED }

    /** Called by the network layer when the session can no longer be refreshed. */
    fun invalidate(reason: Reason = Reason.SESSION_REPLACED) {
        store.clear()
        _evictions.tryEmit(reason)
    }
}
