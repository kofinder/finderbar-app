package com.finderbar.jovian

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy;
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.FirebaseApp
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.Iconics
import com.mikepenz.pixeden_7_stroke_typeface_library.Pixeden7Stroke
import com.orhanobut.logger.Logger.VERBOSE
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.vanniktech.emoji.ios.IosEmojiProvider
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.MediaSourceBuilder
import im.ene.toro.exoplayer.ToroExo
import okhttp3.OkHttpClient;
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by FinderBar on 10/17/18.
 */
val prefs: Prefs by lazy {
    App.prefs!!
}

val apolloClient: ApolloClient by lazy {
    App.apolloClient!!
}

class App : Application() {

    companion object {
        var apolloClient : ApolloClient? = null
        var prefs: Prefs? = null
        var config: Config? = null
        var cacheFile = 2 * 1024 * 1024.toLong() // size of each cache file.
        var exoCreator: ExoCreator? = null

        const val BASE_URL : String = ""
        const val SUBSCRIPTION_BASE_URL: String = ""
        const val SQL_CACHE_NAME = "apolloDB"
        const val TIMEOUT_SECONDS: Long = 10
    }


    override fun onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this)
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);

        Iconics.init(applicationContext)
        Iconics.registerFont(Pixeden7Stroke())
        Iconics.registerFont(FontAwesome())

        EmojiManager.install(IosEmojiProvider());
        EmojiManager.install(GoogleEmojiProvider());
        EmojiManager.install(TwitterEmojiProvider());

        prefs = Prefs(applicationContext)
        val okHttpClient = OkHttpClient
                .Builder()
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(LoggingInterceptor.Builder()
                        .setLevel(Level.BASIC)
                        .log(VERBOSE)
                        .addHeader("x-auth-id", prefs!!.userId)
                        .addHeader("x-auth-token", prefs!!.authToken)
                        .build()
                ).build()
//                .addInterceptor { chain ->
//                    val original = chain.request()
//                    val builder = original.newBuilder().method(original.method, original.body)
//                    builder.addHeader("x-auth-id", prefs!!.userId) .addHeader("x-auth-token", prefs!!.authToken)
//                    chain.proceed(builder.build())
//                }.build()

        val cacheKeyResolver = object : CacheKeyResolver() {
            override fun fromFieldRecordSet(field: ResponseField, recordSet: Map<String, Any>): CacheKey {
                val typeName = recordSet["__typename"] as String
                if ("Auth" == typeName) {
                    val userKey = typeName + "." + recordSet["signIn"]
                    return CacheKey.from(userKey)
                }
                if (recordSet.containsKey("id")) {
                    val typeNameAndIDKey = recordSet["__typename"].toString() + "." + recordSet["id"]
                    return CacheKey.from(typeNameAndIDKey)
                }
                return CacheKey.NO_KEY
            }

            override fun fromFieldArguments(field: ResponseField, variables: Operation.Variables): CacheKey {
                return CacheKey.NO_KEY
            }
        }

        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .normalizedCache(LruNormalizedCacheFactory(EvictionPolicy.NO_EVICTION)
                .chain(SqlNormalizedCacheFactory(ApolloSqlHelper(this, SQL_CACHE_NAME))), cacheKeyResolver)
                .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))
                .build()

        val cache = SimpleCache(File(cacheDir.absolutePath + "/media_cache" ),
                LeastRecentlyUsedCacheEvictor(cacheFile))
        config = Config.Builder()
                .setMediaSourceBuilder(MediaSourceBuilder.LOOPING)
                .setCache(cache)
                .build()
        exoCreator = ToroExo.with(this).getCreator(config)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level);
        if (level >= TRIM_MEMORY_BACKGROUND) ToroExo.with(this).cleanUp();
    }
}
