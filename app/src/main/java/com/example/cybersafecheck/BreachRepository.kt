package com.example.cybersafecheck

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class BreachRepository {

    private val pwnedPasswordsApi: PwnedPasswordsApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pwnedpasswords.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        pwnedPasswordsApi = retrofit.create(PwnedPasswordsApi::class.java)
    }

    suspend fun fetchRange(prefix: String): String = withContext(Dispatchers.IO) {
        pwnedPasswordsApi.checkRange(prefix).use { body ->
            body.string()
        }
    }
}