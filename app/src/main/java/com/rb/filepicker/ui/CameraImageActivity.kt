package com.rb.filepicker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.rb.filepicker.R
import com.rb.filepicker.adapter.GalleryActivity
import com.rb.filepicker.databinding.ActivityCameraImageBinding
import com.rb.filepicker.databinding.ActivityMainBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraImageActivity : AppCompatActivity() {
private lateinit var binding: ActivityCameraImageBinding
private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
private lateinit var cameraSelector: CameraSelector
private var imageCapture: ImageCapture? = null
private lateinit var imgCaptureExecutor: ExecutorService
var cameraFacing = CameraSelector.LENS_FACING_BACK

private val cameraPermissionResult =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            startCamera()
        } else {
            Snackbar.make(
                binding.root,
                "The camera permission is necessary",
                Snackbar.LENGTH_INDEFINITE
            ).show()

        }
    }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCameraImageBinding.inflate(layoutInflater)
    setContentView(binding.root)

    cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    imgCaptureExecutor = Executors.newSingleThreadExecutor()


    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        cameraPermissionResult.launch(Manifest.permission.CAMERA);
    } else {
        startCamera()
    }

    binding.imgCaptureBtn.setOnClickListener {
        takePhoto()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            animateFlash()
        }
    }

    binding.switchBtn.setOnClickListener {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }
    binding.galleryBtn.setOnClickListener {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
}

private fun startCamera() {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(binding.preview.surfaceProvider)
    }
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        imageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            Log.d(TAG, "Use case binding failed")
        }
    }, ContextCompat.getMainExecutor(this))
}

private fun takePhoto() {
    imageCapture?.let {
        val fileName = "JPEG_${System.currentTimeMillis()}"
        val file = File(externalMediaDirs[0], fileName)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        it.takePicture(
            outputFileOptions,
            imgCaptureExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.i(TAG, "The image has been saved in ${file.toUri()}")
                    val intent = Intent(this@CameraImageActivity, GalleryActivity::class.java)
                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        binding.root.context,
                        "Error taking photo",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "Error taking photo:$exception")
                }

            })
    }
}

private fun animateFlash() {
    binding.root.postDelayed({
        binding.root.foreground = ColorDrawable(Color.WHITE)
        binding.root.postDelayed({
            binding.root.foreground = null
        }, 50)
    }, 100)
}

companion object {
    val TAG = "CameraImageActivity"
}
}
