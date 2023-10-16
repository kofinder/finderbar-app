package com.finderbar.jovian.utilities.android

import android.content.Context
import android.content.res.Configuration


fun Context.isXLargeTablet(context: Context) = context.resources.configuration.screenLayout and
    Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
