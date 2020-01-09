package com.example.retrofitdemo.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object WebAccess {
    val partsApi: PartsApiClient by lazy {
        Log.d("WebAccess", "Creating retrofit client")
        val retrofit = Retrofit.Builder()
            // The 10.0.2.2 address routes request from the Android emulator
            // to the localhost / 127.0.0.1 of the host PC
            .baseUrl("http://10.0.2.2:3000/")
            // Moshi maps JSON to classes
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return@lazy retrofit.create(PartsApiClient::class.java)
    }
}