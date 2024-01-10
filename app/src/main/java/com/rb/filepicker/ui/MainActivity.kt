package com.rb.filepicker.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rb.filepicker.common.getFile
import com.rb.filepicker.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        const val tag = "MainActivity"
    }
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgCaptureBtn.setOnClickListener {
            getFile(tag, supportFragmentManager){ file ->
                GlobalScope.launch(Dispatchers.Main) {
                    Log.d(tag, file.path)
                    Glide.with(binding.root).load(file).into(binding.imageView)
                }
            }
        }
    }
}