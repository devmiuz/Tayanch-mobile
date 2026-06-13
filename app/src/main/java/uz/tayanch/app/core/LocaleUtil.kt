package uz.tayanch.app.core

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Tayanch is Uzbek-first: the UI must render in Uzbek regardless of the device's
 * system language. We achieve that by wrapping the base [Context] of both the
 * Application and the Activity with an Uzbek [Configuration], so every
 * `stringResource` / `getString` call resolves against `res/values` (Uzbek).
 *
 * `res/values-en` is kept as a dormant fallback for a future in-app language
 * switcher; it no longer auto-applies just because the phone is set to English.
 */
object LocaleUtil {
    const val APP_LANG = "uz"

    fun wrap(base: Context, language: String = APP_LANG): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }
}
