package com.example.cybersafecheck

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface PwnedPasswordsApi {
    @GET("range/{prefix}")
    suspend fun checkRange(@Path("prefix") prefix: String): ResponseBody
}