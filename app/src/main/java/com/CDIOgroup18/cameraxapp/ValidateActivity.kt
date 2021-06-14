package com.CDIOgroup18.cameraxapp


import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_validate.*
import java.io.File
import android.graphics.Bitmap;
import coil.load
import okhttp3.OkHttpClient


class ValidateActivity : AppCompatActivity()  {

    private var savedUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate)

        undoButton.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        goButton.setOnClickListener {
            intent = Intent(this, ResponseActivity2::class.java)
            startActivity(intent)
        }
        savedUri=intent.getStringExtra("imagePath").toString()
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

}