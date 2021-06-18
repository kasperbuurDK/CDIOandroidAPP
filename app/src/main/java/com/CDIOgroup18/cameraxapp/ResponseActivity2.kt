package com.CDIOgroup18.cameraxapp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_response2.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream


class ResponseActivity2 : AppCompatActivity() {

    private val client = OkHttpClient()

    private lateinit var bitmap: Bitmap

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
        setContentView(R.layout.activity_response2)


        newMoveButton.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "nextMove")
            startActivity(intent)
        }

        decline_move_button.setOnClickListener {
            intent = Intent(this, TakePhotoActivity::class.java)
            intent.putExtra("status", "userDeclined")
            startActivity(intent)
        }

        Thread {
            val request = Request.Builder()
                .url("http://130.225.170.93:9001/api/v1/download/${MainActivity.myGameID}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val inputStream: InputStream = response.body!!.byteStream()
                bitmap = BitmapFactory.decodeStream(inputStream)

                //println(response.body!!.string())

                //uncomment next line

                // cardMoveImg.setImageBitmap(bitmap)
                //imageTakenView.load()
                //val mImg: ImageView
                //mImg = findViewById<View>(R.id.imageView2) as ImageView
                //mImg.setImageBitmap(bmOut)
                runOnUiThread {
                    cardMoveImg.setImageBitmap(bitmap)
                }
            }

        }.start()




    }

}








///////////

/* var bgThread: Executor =
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
        println("IMAGEPATH ======== $imagePath")

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

*/
