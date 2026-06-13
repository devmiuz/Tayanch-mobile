package uz.tayanch.app.ui.quiz

import uz.tayanch.app.data.dto.QuizSubmitRequest
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Pillar 5 — offline-first submission queue. When grading can't reach the server,
 * the answer is parked here with an implicit PENDING status. In production a
 * WorkManager job observes connectivity and drains the queue, so a dropped 4G
 * connection never costs the user their earned score. (In-memory here for the demo.)
 */
object OfflineAnswerQueue {
    private val pending = CopyOnWriteArrayList<QuizSubmitRequest>()

    val size: Int get() = pending.size

    fun enqueue(submission: QuizSubmitRequest) { pending.add(submission) }

    fun drainTo(sync: (QuizSubmitRequest) -> Boolean) {
        pending.toList().forEach { item -> if (sync(item)) pending.remove(item) }
    }
}
