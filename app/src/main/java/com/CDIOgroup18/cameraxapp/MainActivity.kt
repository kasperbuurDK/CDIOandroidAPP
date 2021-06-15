package com.CDIOgroup18.cameraxapp


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
        var myGameID : Int? = -1
    }

    private var bgThread: Executor =
        Executors.newSingleThreadExecutor() // handle for backgroundThread (network com)
    private var uiThread = Handler(Looper.getMainLooper()) // handle for activity


    private var status: String? = null
    var ourMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar2.visibility = View.GONE

        status = if (intent.getStringExtra("status") != null) {
            intent.getStringExtra("status")
        } else {
            "just_started"
        }

        ourMessage = when (status) {
            "just_started" -> {
                "Welcome to SmartSolitareSolver\n" +
                        "Enjoy the game"
            }
            "fromTakePhoto" -> {
                "Returned from TakePhoto\n " +
                        "Your gameID is still $myGameID"
            }
            else -> {
                "No known status"
            }
        }

        activeGame_button.setOnClickListener { goToTakePhoto() }
        startNewGame_Button.setOnClickListener { startNewGameAtServer() }
        requestID_Button.setOnClickListener { getIDfromServer() }
        dev_button_1_toTakePhoto.setOnClickListener { goToTakePhoto() }
        dev_button_2_toValidate.setOnClickListener { goToValidate()}

        updateUserView()

    }

    private fun goToValidate() {
        intent = Intent(this, ValidateActivity::class.java)
        startActivity(intent)
    }

    private fun updateUserView() {
        val needGameID = "Need ID to use"

        textView.text = ourMessage

        if (myGameID == -1) {
            activeGame_button.isEnabled = false
            startNewGame_Button.isEnabled = false
            activeGame_button.text = needGameID
            startNewGame_Button.text = needGameID

        } else {
            activeGame_button.isEnabled = true
            startNewGame_Button.isEnabled = true
            activeGame_button.text = "To active game"
            startNewGame_Button.text = "Start new game"
        }


    }

    private fun startNewGameAtServer() {

        progressBar2.visibility = View.VISIBLE
        textView.text = "Contacting server to start new game"

        if (myGameID != -1) {
            bgThread.execute(Runnable {
                try {
                    val thread = RestartToServerClass()
                    thread.start()

                    uiThread.post(Runnable {
                        progressBar2.visibility = View.GONE
                        textView.text = "Game reset at at server"

                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    textView.text = "Failed to restart game at server"
                    progressBar2.visibility = View.GONE
                }
            })
        }

        updateUserView()

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
        updateUserView()

    }

    override fun onResume() {
        super.onResume()

        status = if (intent.getStringExtra("status") != null) ({
        }).toString() else "just_started"

        println("\n IN onResume status is: $status")

        updateUserView()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun getIDfromServer() {

        ourMessage = "Please wait contacting server....\n " +
                "Obtaning game ID"

        var backFromServer = 0

        try {
            val thread = StartMessageToServer()
            thread.start()
            backFromServer = 1

        } catch (e: Exception) {
            e.printStackTrace()
            backFromServer = 2
        }

        println("IS RUN DONE?")

        while (backFromServer == 0)

            ourMessage = if (backFromServer == 1) "Your game ID is: $myGameID"
            else if (backFromServer == 2) "Failed to acquire ID from server"
            else "Could not determine server respons "

        updateUserView()
    }
}
