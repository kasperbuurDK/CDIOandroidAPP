package com.CDIOgroup18.cameraxapp

import android.content.Intent
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.Coil
import coil.load
import kotlinx.android.synthetic.main.activity_response2.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ResponseActivity2 : AppCompatActivity() {

    var bgThread: Executor =
        Executors.newSingleThreadExecutor() // handle for backgroundThread (network com)
    var uiThread = Handler(Looper.getMainLooper()) // handle for activity

    private lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response2)

        declineButton.setOnClickListener { declinedAnswer() }
        acceptButton.setOnClickListener { acceptedAnswer() }
        testButton.setOnClickListener { getImageFromWeb() }

        progressBar.visibility = View.GONE

        imagePath = intent.getStringExtra("imagePath").toString()



    }

    private fun getRSSFromWeb() {

        progressBar.visibility = View.VISIBLE
        testButton.text = "henter"

        bgThread.execute(Runnable {
            try {
                val rssData: String = hentUrl("https://www.version2.dk/it-nyheder/rss")

                uiThread.post(Runnable {
                    var somethingToDisplay: String = rssData.substring(0, 20)
                    progressBar.visibility = View.GONE
                    testButton.text = somethingToDisplay

                })

            } catch (e: Exception) {
                e.printStackTrace()
                uiThread.post(Runnable {
                    testButton.text = "fejl"
                    progressBar.visibility = View.GONE
                })
            }
        })

    }

    private fun getImageFromWeb() {

        val imageURL =
            "https://asset.dr.dk/imagescaler/?protocol=https&server=www.dr.dk&file=%2Fimages%2Fcrop%2F2021%2F06%2F08%2F1623136413_20210420-124236-1-1920x804we.jpg&scaleAfter=crop&quality=70&w=850&h=478"
        progressBar.visibility = View.VISIBLE
        disableAllButtons()

        bgThread.execute(Runnable {
            try {
                imageView2.load(imageURL)

                uiThread.post(Runnable {
                    progressBar.visibility = View.GONE
                    enableAllButtons()
                })

            } catch (e: Exception) {
                e.printStackTrace()
                uiThread.post(Runnable {
                    progressBar.visibility = View.GONE
                    enableAllButtons()
                })
            }
        })

    }

    private fun disableAllButtons() {
        testButton.isEnabled = false
        acceptButton.isEnabled = false
        declineButton.isEnabled = false
    }

    private fun enableAllButtons() {
        testButton.isEnabled = true
        acceptButton.isEnabled = true
        declineButton.isEnabled = true
    }

    private fun acceptedAnswer() {
        val toMainIntent = Intent(this, MainActivity::class.java)

        startActivity(toMainIntent)

    }

    private fun declinedAnswer() {
        Toast.makeText(this, "I made a TOAST $imagePath", Toast.LENGTH_SHORT).show()
        imageView2.load(imagePath)
    }

    @Throws(IOException::class)
    fun hentUrl(url: String): String {
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

