package com.CDIOgroup18.cameraxapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_validate.*


class ValidateActivity : AppCompatActivity()  {

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


    }





}