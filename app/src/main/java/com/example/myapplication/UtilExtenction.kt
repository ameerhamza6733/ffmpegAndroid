package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

fun Context.createVideoFile(fileExtension:String): File?{
    val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timestamp + "_"
    val storageDir: File = this.filesDir
    return File.createTempFile(imageFileName, "$fileExtension", storageDir)
}

fun File.copyInputStreamToFile(inputStream: InputStream?) {
    this.outputStream().use { fileOut ->
        inputStream?.copyTo(fileOut)
    }
}

fun Activity.log(tag:String){
    android.util.Log.d(this.localClassName,tag)
}

fun ViewModel.log(tag:String){
    android.util.Log.d(this.toString(),tag)
}