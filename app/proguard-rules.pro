# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-keep public class * extends com.bumptech.glide.module.AppGlideModule

-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

-keep class android.arch.lifecycle.** {*;}

-keep class android.support.v7.widget.SearchView { *; }

-keep public class android.support.v7.widget.** { *; }

-keep public class android.support.v7.internal.widget.** { *; }

-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-keep class com.facebook.FacebookSdk {
   boolean isInitialized();
}
-keep class com.facebook.appevents.AppEventsLogger {
   com.facebook.appevents.AppEventsLogger newLogger(android.content.Context);
   void logSdkEvent(java.lang.String, java.lang.Double, android.os.Bundle);
}

-dontnote com.google.android.exoplayer2.ext.ima.ImaAdsLoader
-keepclassmembers class com.google.android.exoplayer2.ext.ima.ImaAdsLoader {
  <init>(android.content.Context, android.net.Uri);
}

-keepclassmembernames class com.google.android.exoplayer2.ui.PlayerControlView {
  java.lang.Runnable hideAction;
  void hideAfterTimeout();
}

-keep com.mikepenz.ico_typeface_library.ico { *; }
-keep class com.mikepenz.iconics.** { *; }
-keep class com.mikepenz.fontawesome_typeface_library.FontAwesome
-keep class com.mikepenz.google_material_typeface_library.GoogleMaterial
-keep class .R
-keep class **.R$* {
    <fields>;
}


-keepattributes Signature
-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }
