package com.CDIOgroup18.cameraxapp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class RestartToServerClass :Thread() {

    private val client = OkHttpClient()

    override fun run() {
        val request = Request.Builder()
            .url("http://130.225.170.93:9001/api/v1/restart/${MainActivity.myGameID}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

        }


    }
}