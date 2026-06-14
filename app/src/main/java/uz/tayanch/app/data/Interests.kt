package uz.tayanch.app.data

/**
 * The learning interests (career fields) a candidate picks during onboarding.
 * Each interest has its own roadmap + content in the mock layer; the user's
 * selected ids drive the Home roadmap, the global quiz, and the Arena deck.
 *
 * Single source of truth shared by onboarding (catalog), the mock backend
 * (what it serves), SecureSessionStore (default when nothing is stored yet),
 * and HomeScreen (switcher labels).
 */
object Interests {

    data class Interest(val id: String, val name: String, val emoji: String)

    val all: List<Interest> = listOf(
        Interest("int-cyber", "Kiberxavfsizlik", "🛡️"),
        Interest("int-python", "Python dasturlash", "🐍"),
        Interest("int-design", "Grafik dizayn", "🎨"),
        Interest("int-android", "Android dasturlash", "🤖"),
    )

    val defaultIds: List<String> = all.map { it.id }

    fun name(id: String): String = all.firstOrNull { it.id == id }?.name ?: id

    fun emoji(id: String): String = all.firstOrNull { it.id == id }?.emoji.orEmpty()

    /** Keep only ids we recognise, preserving catalog order; fall back to all. */
    fun resolve(ids: List<String>): List<Interest> {
        val picked = all.filter { it.id in ids }
        return picked.ifEmpty { all }
    }
}
