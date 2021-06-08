package com.CDIOgroup18.cameraxapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import kotlinx.android.synthetic.main.activity_response2.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ResponseActivity2 : AppCompatActivity() {

    var bgThread: Executor = Executors.newSingleThreadExecutor() // handle for backgroundThread (network com)
    var uiThread = Handler(Looper.getMainLooper()) // handle for activity

    private lateinit var imagePath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response2)

        declineButton.setOnClickListener { declinedAnswer() }

        acceptButton.setOnClickListener { acceptedAnswer() }

        testButton.setOnClickListener { getSomethingFromWeb() }

        progressBar.visibility = View.GONE

        imagePath = intent.getStringExtra("imagePath").toString()

    }

    private fun getSomethingFromWeb() {

        bgThread.execute(Runnable {
            try {
                uiThread.post(Runnable {
                    testButton.text = "arbejder"
                    progressBar.visibility = View.VISIBLE
                })
                val rssData: String = hentUrl("https://www.version2.dk/it-nyheder/rss")
                var somethingToDisplay: String = rssData.substring(0, 20)
                testButton.text = somethingToDisplay

            } catch (e: Exception) {
                e.printStackTrace()
                uiThread.post(Runnable {
                    testButton.text = "fejl"
                    progressBar.visibility = View.GONE
                })
            }
        })

    }

    private fun acceptedAnswer() {
        val toMainIntent = Intent(this, MainActivity::class.java)

        startActivity(toMainIntent)

    }

    private fun declinedAnswer() {
        Toast.makeText(this, "I made a TOAST $imagePath", Toast.LENGTH_SHORT).show()
       // imageLoader.
        imageView2.load(imagePath)

    }

    @Throws(IOException::class)
    fun hentUrl(url: String): String {
        println("Henter $url")
        val br = BufferedReader(InputStreamReader(URL(url).openStream()))
        val sb = StringBuilder()
        var linje = br.readLine()
        while (linje != null) {
            sb.append(
                """
                $linje
                
                """.trimIndent()
            )
            linje = br.readLine()
            println(linje)
        }
        br.close()
        return sb.toString()
    }


}

