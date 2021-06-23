package com.CDIOgroup18.cameraxapp


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CameraXBasic"
        var myGameID = -1
        var validID = false
    }

    private var status: String? = null
    private var ourMessage = ""

    private lateinit var ourSharedPref: SharedPreferences

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        removeTitleAndActionBar()

        setContentView(R.layout.activity_main)
        progressBar2.visibility = View.GONE

        setOnClickListeners()

        prepareScreen()

    }

    private fun prepareScreen() {
        Thread {

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
                    "Returned from TakePhoto"
                }
                "comError" -> {
                    "Communication error with server \n " +
                            "Please try again or request new ID"
                }
                "no_reponse" -> {
                    "Server failed to response with photo"
                }
                else -> {
                    "No known status"
                }

            }

            runOnUiThread { updateUserView() }

        }.start()
    }

    private fun setOnClickListeners() {
        activeGame_button.setOnClickListener { goToTakePhoto() }
        startNewGame_Button.setOnClickListener { startNewGameAtServer() }
        requestID_Button.setOnClickListener { getIDfromServer() }
        endAtServer_button.setOnClickListener { deleteAccountAtServer() }
    }

    private fun removeTitleAndActionBar() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun getIDfromServer() {

        Thread {


            /* SERVERVERSION
            val request = Request.Builder()
                .url("http://130.225.170.93:9001/api/v1/start")
                .build()
            */

            // LOCAL VERSION
            val request = Request.Builder()
                .url("http://10.16.160.41:8080/api/v1/start")
                .build()



            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }

                println("DEBUG_HEY" + response.body!!.string())

                val responseGameID = response.header("gameID")

                myGameID = responseGameID!!.toInt()

                println("responseGameID is: $responseGameID")
                println("MainActivity.myGameID is: ${myGameID}")

            }
            runOnUiThread { updateUserView() }
        }.start()

    }

    private fun startNewGameAtServer() {

        progressBar2.visibility = View.VISIBLE
        textView.text = "Contacting server to start new game"

        Thread {

            /* SERVERVERSION
            val request = Request.Builder()
                .url("http://130.225.170.93:9001/api/v1/restart/$myGameID")
                .build()

             */

            // LOCAL VERSION
            val request = Request.Builder()
                .url("http://10.16.160.41:8080/api/v1/restart/$myGameID")
                .build()


            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                println("HELLLLO from response: ${response.body!!.string()}")
            }

            runOnUiThread {
                progressBar2.visibility = View.GONE
                ourMessage = "Game reset at at server"
                println("HELLO from UIThread")
                updateUserView()
            }

        }.start()
    }

    private fun deleteAccountAtServer() {
        progressBar2.visibility = View.VISIBLE
        textView.text = "Contacting server to delete account"

        Thread {
            try {

                /* SERVERVERSION
                val request = Request.Builder()
                    .url("http://130.225.170.93:9001/api/v1/end/$myGameID")
                    .build()
                 */

                     // LOCALVERSION
                val request = Request.Builder()
                    .url("http://10.16.160.41:8080/api/v1/end/$myGameID")
                    .build()



                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println("DEBUG_HEY" + response.body!!.string())

                    ourMessage = "Game reset at at server"
                    myGameID = -1
                    validID = false

                    runOnUiThread {
                        progressBar2.visibility = View.GONE
                        updateUserView()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ourMessage = "Failed to restart game at server"
                textView.text = "Failed to restart game at server"
                progressBar2.visibility = View.GONE
                runOnUiThread { updateUserView() }
            }

        }.start()

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


    private fun goToTakePhoto() {
        intent = Intent(this, TakePhotoActivity::class.java)
        intent.putExtra("status", "resumed")
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


}
