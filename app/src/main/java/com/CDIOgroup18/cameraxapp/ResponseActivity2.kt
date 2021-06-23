package com.CDIOgroup18.cameraxapp

import android.app.AlertDialog
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
import java.lang.Exception

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

        // Button to accept image and start a new move
        newMoveButton.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "nextMove")
            startActivity(intent)
        }

        // Button to decline the image
        decline_move_button.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "userDeclined")
            startActivity(intent)
        }

        // Get image reponse from server and show it
        Thread {

            //SERVERVERSION
            /*
            val request = Request.Builder()
                .url("http://130.225.170.93:9001/api/v1/download/${MainActivity.myGameID}")
                .build()
             */

            //LOCALVERSION
            val request = Request.Builder()
                .url("http://10.16.160.41:8080/api/v1/download/${MainActivity.myGameID}")
                .build()


            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val inputStream: InputStream = response.body!!.byteStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    runOnUiThread {
                        cardMoveImg.setImageBitmap(bitmap)
                    }
                }
            } catch(e : Exception){
                e.printStackTrace()
                    runOnUiThread {
                        alDialog("Server error: No response image sent")
                    }
            }
        }.start()
    }

    // Error dialog
    private fun alDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error detected!")
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // Performing on ok button
        builder.setPositiveButton("OK") { dialogInterface, which ->
            goToTakePhoto("no_response")

            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    private fun goToTakePhoto(intentString: String) {
        intent = Intent(this, TakePhotoActivity::class.java)
        if (intentString != "noIntentString") {
            intent.putExtra("status", intentString)
        }
        startActivity(intent)
    }
}