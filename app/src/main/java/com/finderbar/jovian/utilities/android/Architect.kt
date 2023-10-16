package com.finderbar.jovian.utilities.android

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

inline fun <reified T : ViewModel> FragmentActivity.viewModel(): T =
    ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.viewModel(): T =
    ViewModelProviders.of(this).get(T::class.java)

inline fun <T> LifecycleOwner.observe(liveData: LiveData<T>, crossinline task: (T?) -> Unit) {
    liveData.observe(this, Observer<T> { t -> task(t) })
}
