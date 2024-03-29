package com.CDIOgroup18.cameraxapp

/**
 * Koden i ValidateActivity og tilhørende layout er primært lavet af
 * Peter Tran, s010219
 */

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import coil.load

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
    private var responseBody = ""
    private lateinit var outputDirectory: File

    private val client = OkHttpClient.Builder()
        // Timeout set to a longer periode, as time is needed for entering cards manually
        .connectTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.MINUTES)
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

        responseBody = ""
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

            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val imageViewParams = imageTakenView.layoutParams as ViewGroup.MarginLayoutParams

            imageViewParams.height = screenHeight
            imageViewParams.width = screenWidth
            imageViewParams.marginStart = 0
            imageViewParams.topMargin = 0

            imageTakenView.layoutParams = imageViewParams

            Thread {
                val file = File(outputDirectory, "aPhoto.jpg")

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.name, file.asRequestBody(MEDIA_TYPE_JPG))
                    .build()

                //SERVERVERSION
                val request = Request.Builder()
                    .url("http://130.225.170.93:9001/api/v1/upload/${MainActivity.myGameID}")
                    .post(requestBody)
                    .build()

                //LOCALVERSION
                /*
                val request = Request.Builder().
                url("http://10.16.160.41:8080/api/v1/upload/${MainActivity.myGameID}").
                        post(requestBody).build()*/

                //Send photo
                try {
                    client.newCall(request).execute().use { response ->
                        responseBody = response.body!!.string()
                        if (responseBody.contains("Kort") ||
                                responseBody.contains(("Træk")) ||
                                responseBody.contains("Spillet"))  {
                            goToResponse();
                        } else if (responseBody.contains("bad")) {
                            runOnUiThread {
                                alDialog("Server error: Server could not analyze image")
                            }

                        } else {
                            runOnUiThread {
                                alDialog("Server error: No response image sent")
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
        intent.putExtra("response", responseBody)
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
            if (message == "Server error: Server could not analyze image") {
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