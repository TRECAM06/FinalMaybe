package com.app.finalmaybe

import android.annotation.SuppressLint
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private var ourInstance: Retrofit?=null

    val instance:Retrofit
        @SuppressLint("SuspiciousIndentation")
        get() {
            if(ourInstance == null)
                ourInstance = Retrofit.Builder()
                    .baseUrl("http://suggestqueries.google.com/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                return ourInstance!!
            }

}