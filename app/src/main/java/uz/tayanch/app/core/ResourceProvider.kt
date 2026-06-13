package uz.tayanch.app.core

import android.content.Context
import androidx.annotation.StringRes

/**
 * Resolves localized strings for classes that have no Compose context — namely
 * the ViewModels. Backed by the (Uzbek-wrapped) Application context, so error
 * fallbacks come out in the app's primary language. Provided as a Koin single.
 */
class ResourceProvider(private val context: Context) {
    fun string(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}
