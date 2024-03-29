package com.rb.filepicker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rb.filepicker.adapter.GalleryAdapter
import com.rb.filepicker.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val directory = File(externalMediaDirs[0].absolutePath)
        val files = directory.listFiles() as Array<File>

        val adapter = GalleryAdapter(this,files.reversedArray())
        binding.viewPager.adapter = adapter
    }
}