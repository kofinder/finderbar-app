package com.finderbar.jovian.utilities.android

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.TransitionRes
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import android.view.Window
import android.view.animation.Interpolator
import android.support.v4.util.Pair as AndroidSupportPair

fun Context.transition(@TransitionRes res: Int) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    TransitionInflater.from(this).inflateTransition(res)
} else {
    TODO("VERSION.SDK_INT < KITKAT")
}

fun Fragment.transition(@TransitionRes res: Int) = context!!.transition(res)
fun View.transition(@TransitionRes res: Int) = context.transition(res)

fun Activity.sceneTransition(): Bundle? {
    return if (Build.VERSION.SDK_INT < 21) null else
        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
}

fun Activity.sceneTransition(vararg sharedElements: Pair<View, String>?): Bundle? {
    return if (Build.VERSION.SDK_INT < 21) null else
        ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            *sharedElements.map { it?.toAndroidSupportPair() }.toTypedArray()
        ).toBundle()
}

private fun Pair<View, String>.toAndroidSupportPair() = AndroidSupportPair(first, second)

var Fragment.allowTransitionOverlap: Boolean
    set(value) {
        allowEnterTransitionOverlap = value
        allowReturnTransitionOverlap = value
    }
    get() = throw IllegalAccessError()

var Window.allowTransitionOverlap: Boolean
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        allowEnterTransitionOverlap = value
        allowReturnTransitionOverlap = value
    }
    get() = throw IllegalAccessError()

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Move(context: Context) = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

fun transitionSetOf(vararg transitions: Transition): TransitionSet {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        TransitionSet().apply {
            transitions.forEach { this.addTransition(it) }
        }
    } else {
        TODO("VERSION.SDK_INT < KITKAT")
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Transition.excludeSystemView(): Transition {
    excludeTarget(android.R.id.statusBarBackground, true)
    excludeTarget(android.R.id.navigationBarBackground, true)
    return this
}

val enterDuration: Long = 300
val exitDuration: Long = 250
val sharedElementDuration: Long = 250

val sharedElementEasing = android.support.v4.view.animation.FastOutSlowInInterpolator()
val enterEasing = android.support.v4.view.animation.LinearOutSlowInInterpolator()
val exitEasing = android.support.v4.view.animation.FastOutLinearInInterpolator()

fun Transition.shareElement(
    time: Long = sharedElementDuration,
    easing: Interpolator = sharedElementEasing
): Transition {
    duration = time
    interpolator = easing
    return this
}

fun Transition.enter(
    time: Long = enterDuration,
    delay: Long = 0,
    easing: Interpolator = enterEasing
): Transition {
    duration = time
    startDelay = delay
    interpolator = easing
    return this
}

fun Transition.exit(
    time: Long = exitDuration,
    delay: Long = 0,
    easing: Interpolator = exitEasing
): Transition {
    duration = time
    startDelay = delay
    interpolator = easing
    return this
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
inline fun Activity.setTransition(action: Window.() -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.action()
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
inline fun Fragment.setTransition(action: Fragment.() -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.action()
    }
}
