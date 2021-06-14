package com.CDIOgroup18.cameraxapp


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.CENTER_IN_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.solver.state.State
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import kotlinx.android.synthetic.main.activity_validate.*
import java.io.File
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody


class ValidateActivity : AppCompatActivity() {

    private var savedUri: String? = null
    private lateinit var outputDirectory: File
    private val client = OkHttpClient()
    private var answerOK :Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate)



        undoButton.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        goButton.setOnClickListener {
            println("TRY SEND TO SERVER")

            outputDirectory = getOutputDirectory()

            val layout: ConstraintLayout = findViewById(R.id.validateLayout)
            val progressBar =
                ProgressBar(this@ValidateActivity, null, android.R.attr.progressBarStyleLarge)
            val params = ConstraintLayout.LayoutParams(100, 100)
            //params.addRule(ConstraintLayout.CENTER_IN_PARENT)
            layout.addView(progressBar, params)

            Handler().postDelayed(
                {
                    // This method will be executed once the timer is over
                },
                5000 // value in milliseconds
            )



            Thread {
                //Thread {


                val file = File(outputDirectory, "aPhoto.jpg")

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
                        //println("successful POST"+response.body!!.string())
                        if (response.body!!.string() == "We uploaded the file!") {
                            //go to reponse aktivity
                            //lav en loading i actibvity

                            answerOK = true


                        } else {
                            //error message for the user
                            answerOK = false
                        }

                    }

                }


            }.start()




            if (answerOK==false) {
                sendDialog()
                //goToMain()
            } else {
                sendDialog()//test purpose
                //goToResponse()
            }


            //goto response

        }
        savedUri = intent.getStringExtra("imagePath").toString()
        imageTakenView.load(savedUri)
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

    /*private fun setAnswer(value:Boolean){
        answerOK = value;
    }*/

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

    private fun goToMain() {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun sendDialog() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Title bohoo")
        //set message for alert dialog
        builder.setMessage("Besked")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("OK"){dialogInterface, which ->
            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
            goToMain()
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
}