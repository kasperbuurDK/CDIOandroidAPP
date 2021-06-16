package com.CDIOgroup18.cameraxapp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class StartMessageToServer : Thread() {

    private val client = OkHttpClient()

    override fun run() {
        val request = Request.Builder()
            .url("http://130.225.170.93:9001/api/v1/start")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            for ((name, value) in response.headers) {
                println("$name: $value")
            }

           println("DEBUG_HEY"+response.body!!.string())


            var responseGameID = response.header("gameID")

            MainActivity.myGameID = responseGameID!!.toInt()

            println("responseGameID is: $responseGameID")

            println("MainActivity.myGameID is: ${MainActivity.myGameID}")



        }
    }
}