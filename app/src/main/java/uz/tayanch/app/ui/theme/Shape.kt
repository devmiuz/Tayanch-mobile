package uz.tayanch.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Shared interactive-control design tokens. Buttons and text fields across the
 * app use these so inputs and actions read as one design system — Material 3
 * defaults otherwise give text fields 4.dp corners but buttons a full pill,
 * which looks inconsistent when stacked together.
 */
object TayanchControl {
    /** Corner radius for primary controls (text fields, buttons). */
    val Shape = RoundedCornerShape(12.dp)

    /** Standard height for full-width action buttons. */
    val Height = 56.dp
}
