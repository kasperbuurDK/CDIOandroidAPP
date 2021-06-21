package com.CDIOgroup18.cameraxapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_response2.*
import kotlinx.android.synthetic.main.activity_validate.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit

class ValidateActivity: AppCompatActivity() {
    private var savedUri: String? = null
    private lateinit var outputDirectory: File

    //test purpose
    private var ipAdr : String = ""

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

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

        setContentView(R.layout.activity_validate)

        savedUri = intent.getStringExtra("imagePath").toString()
        imageTakenView.load(savedUri)

        // Undo button, takes user to TakePhotoActivity
        undoButton.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "fromValidate")
            startActivity(intent)
        }

        // Go button, photo accepted by user, send the photo
        goButton.setOnClickListener {
            outputDirectory = getOutputDirectory()
            val proLayout: ConstraintLayout = findViewById(R.id.progressLayout)
            proLayout.isVisible = true
            goButton.isEnabled = false
            undoButton.isEnabled = false

            //test purpose
            ipAdr = textInput.text.toString()


            Thread {
                val file = File(outputDirectory, "aPhoto.jpg")

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.name, file.asRequestBody(MEDIA_TYPE_JPG))
                    .build()

                val request = Request.Builder()
                   // .url("http://130.225.170.93:9001/api/v1/upload/${MainActivity.myGameID}")
                   //130.225.170.93:9001
                    .url("http://" + ipAdr +"/api/v1/upload/${MainActivity.myGameID}")
                    .post(requestBody)
                    .build()

                var responseBody = ""

                //Send photo
                try {
                    client.newCall(request).execute().use { response ->
                        responseBody = response.body!!.string()

                        when (responseBody) {
                            "We uploaded the file!" -> {
                                goToResponse()
                            }
                            "bad_image" -> {
                                runOnUiThread {
                                alDialog("Server error: Server could not analyze image")
                               }
                            }
                            else -> {
                                runOnUiThread {
                                    alDialog("Server error: No response image sent")
                                }
                            }
                        }
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                    if (responseBody.contains("Internal Server Error")) {
                        runOnUiThread {
                            alDialog("Server error: No response image sent")
                        }
                    } else {
                        runOnUiThread {
                        alDialog("Communication error: Photo was not sent")
                        }
                    }
                }
            }.start()
        }
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

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun goToResponse() {
        intent = Intent(this, ResponseActivity2::class.java)
        startActivity(intent)
    }

    private fun goToTakePhoto(intentString: String) {
        intent = Intent(this, TakePhotoActivity::class.java)
        if (intentString != "noIntentString") {
            intent.putExtra("status", intentString)
        }
        startActivity(intent)
    }

    // Error dialog
    private fun alDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error detected!")
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // Performing on ok button
        builder.setPositiveButton("OK") { dialogInterface, which ->
            if (message == "Server could not analyze image") {
                goToTakePhoto("bad_image")
            } else if (message == "commnunication no good") {
                goToTakePhoto("comError")
            } else if (message == "Internal Server Error") {
                goToTakePhoto("internal_server_error")
            } else {
                goToTakePhoto("noStatusIntent")
            }
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    companion object {
        private val MEDIA_TYPE_JPG = "image/jpeg".toMediaType()
    }
}