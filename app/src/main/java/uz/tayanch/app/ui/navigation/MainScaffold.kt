package uz.tayanch.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import uz.tayanch.app.R
import uz.tayanch.app.ui.career.CareerScreen
import uz.tayanch.app.ui.home.HomeScreen
import uz.tayanch.app.ui.jobs.JobsScreen
import uz.tayanch.app.ui.profile.ProfileScreen

private enum class HubTab(@StringRes val titleRes: Int, val icon: ImageVector) {
    HOME(R.string.nav_home, Icons.Filled.School),
    JOBS(R.string.nav_jobs, Icons.Filled.Work),
    CAREER(R.string.nav_career, Icons.Filled.RocketLaunch),
    PROFILE(R.string.nav_profile, Icons.Filled.Person),
}

private const val TAB_ANIM_MS = 280

/**
 * Phase 2 of navigation: the three bottom-bar destinations.
 *
 * No pager and no top app bar. Tab switches are animated with a directional
 * [AnimatedContent] slide (left when moving to a later tab, right when going
 * back). The Scaffold owns the [NavigationBar] (its colour fills under the
 * gesture inset) and the Home-only FAB; its [PaddingValues] — which includes the
 * status-bar inset since there is no app bar — is forwarded to each page.
 */
@Composable
fun MainScaffold(
    onOpenContent: (String) -> Unit,
    onOpenFlashcard: (String) -> Unit,
    onOpenQuiz: (contentId: String) -> Unit,
    onOpenBattle: () -> Unit,
    onOpenVacancy: (String) -> Unit,
    onOpenInterview: () -> Unit,
    onOpenResume: () -> Unit,
    onLogout: () -> Unit,
) {
    val tabs = remember { HubTab.entries }
    var selected by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    val label = stringResource(tab.titleRes)
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = { Icon(tab.icon, contentDescription = label) },
                        label = { Text(label) },
                    )
                }
            }
        },
        floatingActionButton = {
            if (selected == HubTab.HOME.ordinal) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.fab_global_quiz)) },
                    icon = { Icon(Icons.Filled.Quiz, contentDescription = null) },
                    onClick = { onOpenQuiz("quiz-global") },
                )
            }
        },
    ) { innerPadding: PaddingValues ->
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                val toLater = targetState > initialState
                val towards = if (toLater) SlideDirection.Left else SlideDirection.Right
                (slideIntoContainer(towards, tween(TAB_ANIM_MS)) + fadeIn(tween(TAB_ANIM_MS)))
                    .togetherWith(slideOutOfContainer(towards, tween(TAB_ANIM_MS)) + fadeOut(tween(TAB_ANIM_MS)))
            },
            label = "hubTab",
        ) { page ->
            when (tabs[page]) {
                HubTab.HOME -> HomeScreen(
                    contentPadding = innerPadding,
                    onOpenContent = onOpenContent,
                    onOpenFlashcard = onOpenFlashcard,
                    onOpenQuiz = onOpenQuiz,
                )
                HubTab.JOBS -> JobsScreen(contentPadding = innerPadding, onOpenVacancy = onOpenVacancy)
                HubTab.CAREER -> CareerScreen(
                    contentPadding = innerPadding,
                    onOpenBattle = onOpenBattle,
                    onOpenInterview = onOpenInterview,
                )
                HubTab.PROFILE -> ProfileScreen(
                    contentPadding = innerPadding,
                    onOpenResume = onOpenResume,
                    onLogout = onLogout,
                )
            }
        }
    }
}
