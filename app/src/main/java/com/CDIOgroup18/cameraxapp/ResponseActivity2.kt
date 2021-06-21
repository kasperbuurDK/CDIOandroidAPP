package com.CDIOgroup18.cameraxapp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_response2.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream


class ResponseActivity2 : AppCompatActivity() {

    private val client = OkHttpClient()

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.activity_response2)


        newMoveButton.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "nextMove")
            startActivity(intent)
        }

        decline_move_button.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "userDeclined")
            startActivity(intent)
        }

        Thread {
            val request = Request.Builder()
                .url("http://130.225.170.93:9001/api/v1/download/${MainActivity.myGameID}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val inputStream: InputStream = response.body!!.byteStream()
                bitmap = BitmapFactory.decodeStream(inputStream)

                runOnUiThread {
                    cardMoveImg.setImageBitmap(bitmap)
                }
            }

        }.start()




    }

}





