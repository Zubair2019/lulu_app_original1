package com.freshcart.shop.ui

import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Motion tokens. Everything moves on a spring, so it can be caught and turned around mid-way; the stiffness sets
 * about how long it takes to settle.
 */
object M {
    /** Small controls: a button changing state. Settles in about 150 ms. */
    fun <T> snap(): SpringSpec<T> = spring(dampingRatio = 0.85f, stiffness = 1500f)
    /** Bars and indicators sliding to a new place. About 300 ms. */
    fun <T> bar(): SpringSpec<T> = spring(dampingRatio = 0.8f, stiffness = 400f)
    /** Sheets, pages and pictures travelling across the screen. About 450 ms. */
    fun <T> sheet(): SpringSpec<T> = spring(dampingRatio = 0.9f, stiffness = 200f)
    /** A little overshoot, for small wins: an item added, a tab chosen. */
    fun <T> pop(): SpringSpec<T> = spring(dampingRatio = 0.5f, stiffness = 500f)
    /** Delay between neighbours that arrive one after another. */
    const val Stagger = 45L
}

/** True when the phone's animations are switched off: loops and entrances are skipped, springs jump to their end. */
@Composable
fun reducedMotion(): Boolean {
    val ctx = LocalContext.current
    return remember { Settings.Global.getFloat(ctx.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
}

/** Keys of elements that have already made their entrance on this screen, so scrolling back doesn't replay them. */
val LocalSeen = compositionLocalOf<MutableSet<Any>?> { null }

/**
 * One-time entrance: fades in and settles from [from] below (or from the side when [sideways]), [index] steps after
 * the first. Draw-only: nothing a customer taps changes place. With an [id] it plays once per screen.
 */
@Composable
fun Modifier.appear(index: Int = 0, id: Any? = null, from: Dp = 18.dp, sideways: Boolean = false): Modifier {
    val seen = LocalSeen.current
    val skip = reducedMotion() || (id != null && seen != null && id in seen)
    val a = remember(id) { Animatable(if (skip) 1f else 0f) }
    LaunchedEffect(id) {
        if (!skip) {
            if (id != null) seen?.add(id)
            delay(index.coerceAtMost(8) * M.Stagger)
            a.animateTo(1f, M.sheet())
        }
    }
    val px = with(LocalDensity.current) { from.toPx() }
    return graphicsLayer {
        alpha = a.value.coerceIn(0f, 1f)
        if (sideways) translationX = (1f - a.value) * px else translationY = (1f - a.value) * px
    }
}

/** One-time entrance for small things: grows from 60% with a little overshoot. Draw-only. */
@Composable
fun Modifier.popIn(index: Int = 0): Modifier {
    val skip = reducedMotion()
    val a = remember { Animatable(if (skip) 1f else 0f) }
    LaunchedEffect(Unit) { if (!skip) { delay(index * M.Stagger * 2); a.animateTo(1f, M.pop()) } }
    return graphicsLayer { alpha = a.value.coerceIn(0f, 1f); val s = 0.6f + 0.4f * a.value; scaleX = s; scaleY = s }
}

/** A number that rolls to its new value: it slides up when it grows and down when it shrinks. */
@Composable
fun RollingText(text: String, value: Double, style: TextStyle, color: Color, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = value to text,
        transitionSpec = {
            val up = targetState.first >= initialState.first
            (slideInVertically(M.bar()) { if (up) it else -it } + fadeIn(M.snap())) togetherWith
                (slideOutVertically(M.bar()) { if (up) -it else it } + fadeOut(M.snap())) using SizeTransform(clip = false)
        },
        label = "roll", modifier = modifier,
    ) { (_, shown) -> Text(shown, style = style, color = color, maxLines = 1, softWrap = false) }
}

/** The scope that lets a picture travel between a product card and the product sheet; null turns the effect off. */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalShared = compositionLocalOf<SharedTransitionScope?> { null }

/** Names the list a product card sits in, so the same product in two lists gets two different travelling pictures. */
val LocalHeroOrigin = compositionLocalOf { "list" }

/**
 * Marks a picture as one end of a journey between a card and the product sheet. Both ends use the same [key]; the
 * end that is [visible] is where the picture rests, and it travels there on a spring when that changes.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.hero(key: String, visible: Boolean): Modifier {
    val scope = LocalShared.current ?: return this
    return with(scope) {
        this@hero.sharedElementWithCallerManagedVisibility(
            rememberSharedContentState(key), visible, boundsTransform = BoundsTransform { _, _ -> M.sheet() },
        )
    }
}
