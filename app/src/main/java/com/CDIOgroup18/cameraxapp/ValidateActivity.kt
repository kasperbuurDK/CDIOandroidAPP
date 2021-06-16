package com.CDIOgroup18.cameraxapp


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import coil.load
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_validate.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit


class ValidateActivity : AppCompatActivity() {

    private var savedUri: String? = null
    private lateinit var outputDirectory: File
    //private val client = OkHttpClient()

    private val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();


    private var answerOK :Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate)

        answerOK = false

        savedUri = intent.getStringExtra("imagePath").toString()
        imageTakenView.load(savedUri)


        undoButton.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            startActivity(intent)
        }
        goButton.setOnClickListener {
            println("TRY SEND TO SERVER")

            println("ID"+MainActivity.myGameID)


            outputDirectory = getOutputDirectory()

            val layout: ConstraintLayout = findViewById(R.id.validateLayout)
            val progressBar =
                ProgressBar(this@ValidateActivity, null, android.R.attr.progressBarStyleLarge)
            val params = ConstraintLayout.LayoutParams(100, 100)
            //params.addRule(ConstraintLayout.CENTER_IN_PARENT)
            layout.addView(progressBar, params)

            buttonDisable(goButton)
            buttonDisable(undoButton)

            //handler is for test purposes, should be deleted
            Handler().postDelayed(
                {
                    // This method will be executed once the timer is over

                    Thread {
                        val file = File(outputDirectory, "aPhoto.jpg")

                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.name, file.asRequestBody(MEDIA_TYPE_JPG))
                            .build()

                        val request = Request.Builder()
                            .url("http://130.225.170.93:9001/api/v1/upload/${MainActivity.myGameID}")
                            .post(requestBody)
                            .build()

                        // client.newCall(request).execute()

                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                println("successful POST"+response.body!!.string())
                                //if (response.body!!.string() == "We uploaded the file!") {
                                  if (true){
                                    goToResponse()
                                } else {
                                    //error message for the user
                                    alDialog("server no good")
                                }
                            }
                            else {  //not sucessful
                                alDialog("commnunication no good")
                            }
                        }
                    }.start()

                },
                3000 // value in milliseconds
            )//end of handler

                    layout.removeView(progressBar)
            buttonEnable(goButton)
            buttonEnable(undoButton)
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

    private fun goToTakePhoto() {
        intent = Intent(this, TakePhotoActivity::class.java)
        startActivity(intent)
    }

    private fun alDialog(message : String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Title bohoo")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("OK"){dialogInterface, which ->
            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
            goToTakePhoto()
        }
        //performing cancel action
        //builder.setNeutralButton("Cancel"){dialogInterface , which ->
        //    Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
        // }
        //performing negative action
        //builder.setNegativeButton("No"){dialogInterface, which ->
        //    Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        //}
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }



    companion object {
        private val MEDIA_TYPE_JPG = "image/jpeg".toMediaType()
    }

    private fun buttonDisable(button: Button) {
        button?.isEnabled = false
        //button?.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
       // button?.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.grey))
    }

    private fun buttonEnable(button: Button) {
        button?.isEnabled = true
        //button?.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
        //button?.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.darkblue))
    }
}