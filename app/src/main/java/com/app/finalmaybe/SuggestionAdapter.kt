package com.app.finalmaybe

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface SuggestionAdapter {
    @GET("complete/search")
    fun getSuggestFromMaps(
        @Query("q") query:String,
        @Query("client") client:String,
        @Query("h1") language:String,
        @Query("ds") restrict:String) : Observable<String>
    @GET("complete/search")
    fun getSuggestFromGoogle(
        @Query("q") query:String,
        @Query("client") client:String,
        @Query("h1") language:String,
        @Query("ds") restrict:String) : Observable<String>
}