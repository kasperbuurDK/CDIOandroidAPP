package com.CDIOgroup18.cameraxapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.imageLoader
import coil.load
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.android.synthetic.main.activity_response2.*
import java.io.IOException
import java.util.*

class ResponseActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response2)

        declineButton.setOnClickListener { run() }

        acceptButton.setOnClickListener { run2() }

    }

    private fun run2() {

        TODO("Not yet implemented")
    }

    private fun run() {
        Toast.makeText(this, "I made a TOAST", Toast.LENGTH_SHORT).show()
       // imageLoader.
        imageView2.load(R.drawable.joaquin)

    }


}

