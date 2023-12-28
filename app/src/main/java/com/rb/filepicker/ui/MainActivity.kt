package com.rb.filepicker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rb.filepicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgCaptureBtn.setOnClickListener {
            val intent = Intent(this, CameraImageActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        val TAG = "MainActivity"
    }
}