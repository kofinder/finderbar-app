
@file:Suppress("UNCHECKED_CAST")

package com.finderbar.jovian.utilities.android
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun <T> FragmentManager.find(id: Int) = findFragmentById(id) as T
fun <T> FragmentManager.find(tag: String) = findFragmentByTag(tag) as T
fun <T> FragmentManager.findFirst(vararg tags: String): T? {
    tags.forEach {
        val fragment = findFragmentByTag(it)
        if (fragment != null)
            return fragment as T
    }
    return null
}

fun FragmentManager.replaceAll(@IdRes id: Int, vararg pair: Pair<String, Fragment>): FragmentTransaction {
    return replaceAll(id, pair.toMap())
}

fun FragmentManager.replaceAll(@IdRes id: Int, map: Map<String, Fragment>): FragmentTransaction {
    val trans = beginTransaction()
    map.forEach {
        val fragment = findFragmentByTag(it.key)
        if (fragment != null) {
            trans.remove(fragment)
        }
        trans.add(id, it.value, it.key)
    }
    return trans
}
