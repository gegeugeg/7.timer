package com.example.coursework2

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GroupActivity : AppCompatActivity() {

    private lateinit var closeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_layout)

        closeButton = findViewById(R.id.backButton)

        closeButton.setOnClickListener {
            finish()
        }
    }
}