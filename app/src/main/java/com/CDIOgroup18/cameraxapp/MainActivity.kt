package com.CDIOgroup18.cameraxapp


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CameraXBasic"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var myGameID = -1
        var validID = false
    }

    private var bgThread: Executor =
        Executors.newSingleThreadExecutor() // handle for backgroundThread (network com)
    private var uiThread = Handler(Looper.getMainLooper()) // handle for activity

    private var status: String? = null
    private var ourMessage = ""

    private lateinit var ourSharedPref: SharedPreferences

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide();
        progressBar2.visibility = View.GONE

        bgThread.execute(Runnable {
            ourSharedPref = this.getPreferences(Context.MODE_PRIVATE)

            myGameID = ourSharedPref.getInt("gameID", -1)

            validID = myGameID != -1 //if mygameID not -1 then validID is true

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

        }

        )

        activeGame_button.setOnClickListener { goToTakePhoto() }
        startNewGame_Button.setOnClickListener { startNewGameAtServer() }
        requestID_Button.setOnClickListener { getIDfromServer() }
        endAtServer_button.setOnClickListener { deleteAccountAtServer() }


        updateUserView()

    }

    private fun deleteAccountAtServer() {
        progressBar2.visibility = View.VISIBLE
        textView.text = "Contacting server to delete account"

        bgThread.execute(Runnable {
            try {
                val thread = EndAtServerClass()
                thread.start()

                uiThread.post(Runnable {
                    progressBar2.visibility = View.GONE
                    ourMessage = "Game reset at at server"
                    textView.text = "Game reset at at server"
                    myGameID = -1
                    validID = false

                })
            } catch (e: Exception) {
                e.printStackTrace()
                ourMessage = "Failed to restart game at server"
                textView.text = "Failed to restart game at server"
                progressBar2.visibility = View.GONE
            }
        })


        updateUserView()
    }

    private fun updateUserView() {
        val needGameID = "Need ID to use"

        validID = myGameID != -1
        
        textView.text = ourMessage
        progressBar2.visibility = View.GONE
        gameIDview.text = "GameID: $myGameID"

        activeGame_button.isEnabled = validID
        startNewGame_Button.isEnabled = validID
        requestID_Button.isEnabled = !validID
        endAtServer_button.isEnabled = validID


        if (!validID) {
            activeGame_button.text = needGameID
            startNewGame_Button.text = needGameID
            endAtServer_button.text = needGameID
            requestID_Button.text = "Get ID from server"

        } else {
            activeGame_button.text = "Continue game"
            startNewGame_Button.text = "Start new game"
            endAtServer_button.text = "Delete account at server"
            requestID_Button.text = "Already have ID"
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

        ourSharedPref.edit().putInt("gameID", myGameID).apply()

    }

    override fun onDestroy() {
        super.onDestroy()
        ourSharedPref.edit().putInt("gameID", myGameID)
    }

    fun getIDfromServer() {

        Thread {

        val request = Request.Builder()
            .url("http://130.225.170.93:9001/api/v1/start")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            for ((name, value) in response.headers) {
                println("$name: $value")
            }

            println("DEBUG_HEY" + response.body!!.string())


            var responseGameID = response.header("gameID")

            MainActivity.myGameID = responseGameID!!.toInt()

            println("responseGameID is: $responseGameID")
            println("MainActivity.myGameID is: ${MainActivity.myGameID}")

            runOnUiThread{updateUserView()}


        }
        }.start()

    }

}
