package com.lee.asher.kotlinio

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by lihong on 2017/8/18.
 */
class RetrofitClient private constructor(val context: Context, val baseUrl: String) {
    var httpCacheDirectory: File? = null
    var cache: Cache? = null
    var okHttpClient: OkHttpClient? = null
    var retrofit: Retrofit? = null
    val defaultTimeOut: Long = 20

    init {
        if (httpCacheDirectory == null) {
            httpCacheDirectory = File(context.cacheDir, "app_cache")
        }
        cache = try {
            cache ?: Cache(httpCacheDirectory, 10 * 1024 * 1024)
        } catch (e: Exception) {
            null
        }
        okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(HttpLoggingInterceptor(
                        HttpLoggingInterceptor.Logger {
                            Log.d("asher", it)
                        }
                ).setLevel(HttpLoggingInterceptor.Level.BODY))
                .cache(cache)
                .addInterceptor(CacheInterceptor(context))
                .connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
                .writeTimeout(defaultTimeOut, TimeUnit.SECONDS)
                .build()
        retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()
    }

    companion object {
        var instance: RetrofitClient? = null
        fun getInstance(context: Context, baseUrl: String): RetrofitClient {
            synchronized(RetrofitClient::class) {
                if (instance == null) {
                    instance = RetrofitClient(context, baseUrl)
                }
            }
            return instance!!
        }
    }

    fun <T> create(service: Class<T>?): T? {
        if (service == null) {
            throw RuntimeException("Api service is null!")
        }
        return retrofit?.create(service)
    }
}