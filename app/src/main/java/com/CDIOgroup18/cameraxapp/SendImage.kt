package com.CDIOgroup18.cameraxapp

import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.*

class SendImage(outputDirectory:File) :Thread() {

    var myFile :  File = outputDirectory
    private val client = OkHttpClient()




    //lav til private
    public override fun run() {

        val file = File(myFile,"aPhoto.jpg")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody(MEDIA_TYPE_JPG))
            .build()

        val request = Request.Builder()
            .url("http://130.225.170.93:9001/api/v1/upload")
            .post(requestBody)
            .build()

       // client.newCall(request).execute()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                println("successful POST"+response.body!!.string())
            }
        }
    }


    companion object {
        private val MEDIA_TYPE_JPG = "image/jpeg".toMediaType()
    }


}
