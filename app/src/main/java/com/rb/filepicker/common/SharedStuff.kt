package com.rb.filepicker.common

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.rb.filepicker.ui.CameraImageDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
    fun getFile(tag : String, supportFragmentManager : FragmentManager, onClicked : (File) -> Unit ) {
        val cameraImageDialog = CameraImageDialog()
        cameraImageDialog.show(supportFragmentManager, "cameraImageDialog")
        cameraImageDialog.openCamera { file ->
            Log.d(tag, "Closed Camera")
            cameraImageDialog.dismiss()
            onClicked(file)
        }
    }
