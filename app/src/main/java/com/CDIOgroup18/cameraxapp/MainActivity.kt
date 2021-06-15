package com.CDIOgroup18.cameraxapp


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CameraXBasic"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var myGameID: Int = -1

    }

    var bgThread: Executor =
        Executors.newSingleThreadExecutor() // handle for backgroundThread (network com)
    var uiThread = Handler(Looper.getMainLooper()) // handle for activity


    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inform user of status
        var ourMessage = ""

        if (intent.getStringExtra("status") != null) {
            status = intent.getStringExtra("status")
        } else {
            status = "just_started"
        }

        println("\n IN onCreate status is: $status")

        when (status) {
            "just_started" -> {

                textView.text = "Please wait contacting server....\n " +
                        "Obtaning game ID"

                bgThread.execute(Runnable {
                    try {
                        val thread = StartMessageToServer()
                        thread.start()

                        uiThread.post(Runnable {
                            ourMessage = "Welcome to SmartSolitareSolver\n " +
                                    "Please make your choice\n " +
                                    "And remember to enjoy\n " +
                                    "Your game ID is: $myGameID"

                            textView.text = ourMessage

                        })

                    } catch (e: Exception) {
                        e.printStackTrace()
                        uiThread.post(Runnable {

                        })
                    }
                })

            }
            "fromTakePhoto" -> {
                ourMessage = "Returned from TakePhoto\n " +
                        "Your gameID is still ${MainActivity.myGameID}"
                textView.text = ourMessage
            }
            else -> {
                ourMessage = "Something is wrong"
                textView.text = ourMessage

            }
        }

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

        status = if (intent.getStringExtra("status") != null) ({
        }).toString() else "just_started"

        println("\n IN onRestart status is: $status")

    }

    override fun onResume() {
        super.onResume()

        status = if (intent.getStringExtra("status") != null) ({
        }).toString() else "just_started"

        println("\n IN onResume status is: $status")

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

    }



}