package com.CDIOgroup18.cameraxapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inform user of status
        var ourMessage = ""

        status = if (intent.getStringExtra("status") != null) ({
        }).toString() else "just_started"

        when (status) {
            "just_started" -> {
                ourMessage = "Welcome to SmartSolitareSolver\n " +
                        "Please make ypur choice\n " +
                        "And remember to enjoy"
            }
            "nextMove" -> {
                ourMessage = "Ready for next photo"
            }
            "no statusIntent" -> {
                ourMessage = "no status"
            }
        }

       textView.text = ourMessage

        toTakePhoto_Button.setOnClickListener{ goToTakePhoto()}
        startNewGame_Button.setOnClickListener{ restartToServer()}

    }

    private fun restartToServer() {
        val thread = RestartToSetverClass()
        thread.start()
    }

    private fun goToTakePhoto() {
        intent = Intent(this, TakePhotoActivity::class.java)
        startActivity(intent)
       }

    override fun onRestart() {
        super.onRestart()

    }

    override fun onResume() {
        super.onResume()



    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

    }



}