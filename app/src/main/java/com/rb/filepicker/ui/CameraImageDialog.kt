package com.rb.filepicker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.rb.filepicker.databinding.ActivityCameraImageBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraImageDialog : DialogFragment() {
private lateinit var binding: ActivityCameraImageBinding
private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
private lateinit var cameraSelector: CameraSelector
private var imageCapture: ImageCapture? = null
private lateinit var imgCaptureExecutor: ExecutorService
var cameraFacing = CameraSelector.LENS_FACING_BACK
    var camera : Camera? = null
    companion object {
        val tag = "CameraImageDialog"
    }

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

    fun  openCamera(listener: Camera) {
        camera = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        Log.d(tag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(tag, "onCreateView")

        binding = ActivityCameraImageBinding.inflate(inflater)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionResult.launch(Manifest.permission.CAMERA);
        } else {
            startCamera()
        }
        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()
            animateFlash()
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
            val intent = Intent(context, GalleryActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(tag, "onViewCreated")

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
            Log.d(tag, "Use case binding failed")
        }
    }, ContextCompat.getMainExecutor(requireActivity()))
}

private fun takePhoto() {
    imageCapture?.let {
        val fileName = "JPEG_${System.currentTimeMillis()}"
        val file = File(requireActivity().externalMediaDirs[0], fileName)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        it.takePicture(
            outputFileOptions,
            imgCaptureExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.i(tag, "The image has been saved in ${file.toUri()}")
                    camera?.getFile(file)
//                    val intent = Intent(requireActivity(), GalleryActivity::class.java)
//                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        binding.root.context,
                        "Error taking photo",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(tag, "Error taking photo:$exception")
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


    fun interface Camera {
        fun getFile(file : File)
    }
}
