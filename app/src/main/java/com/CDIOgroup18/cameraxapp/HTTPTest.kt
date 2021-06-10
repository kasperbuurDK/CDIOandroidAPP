package com.CDIOgroup18.cameraxapp



import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class HTTPTest {

    private val client = OkHttpClient()

    fun run() {
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", "Square Logo")
            .addFormDataPart("image", "logo-square.png",
                File("docs/images/logo-square.png").asRequestBody(MEDIA_TYPE_PNG))
            .build()

        val request = Request.Builder()
            .header("Authorization", "Client-ID $IMGUR_CLIENT_ID")
            .url("https://api.imgur.com/3/image")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            println(response.body!!.string())
        }
    }

    companion object {
        /**
         * The imgur client ID for OkHttp recipes. If you're using imgur for anything other than running
         * these examples, please request your own client ID! https://api.imgur.com/oauth2
         */
        private val IMGUR_CLIENT_ID = "9199fdef135c122"
        private val MEDIA_TYPE_PNG = "image/png".toMediaType()
    }
}