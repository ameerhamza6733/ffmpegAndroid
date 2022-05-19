package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TakeVideoPhotoFromCameraActivity : AppCompatActivity() {


    lateinit var currentPhotoPath: String


    companion object{
        val EXTRA_MEX_DURATION_MILLI="extra_mex_duration "
        val ACTION_TAKE_VIDEO="action take video"
        val ACTION_TAKE_PHOTO="action take photo";
        val RC_TAKE_VIDEO=11;
        val REQUEST_IMAGE_CAPTURE=132;
        val  REQUEST_CAMERA_PERMISSION=24
        val PICK_PHOTO_CODE_URI=64;
    }

    private var takeVideoPending: Boolean = false
    private var takePhotoPending=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_take_video_photo_from_camera)

        performTakeVideoRequest()


    }


    private fun performTakeVideoRequest() {

        if (hasCameraPermission()) {
            when(intent.action){
                ACTION_TAKE_PHOTO->{

                }
                ACTION_TAKE_VIDEO->{
                    takeVideo()
                }
            }
        } else {
            when(intent.action){
                ACTION_TAKE_PHOTO->{
                    takeVideoPending=false
                    takePhotoPending=true
                }
                ACTION_TAKE_VIDEO->{
                    takePhotoPending=false
                    takeVideoPending=true
                }
            }
            requestCameraPermission()
        }
    }



    private fun hasCameraPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionStorge = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permission == PackageManager.PERMISSION_GRANTED && permissionStorge == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (takePhotoPending) {

                    } else if (takeVideoPending) {
                        takeVideo()
                    }

                }
                return
            }
        }
        //pass permission result to fragments in this activity
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun takeVideo() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            var max = intent.getIntExtra(EXTRA_MEX_DURATION_MILLI,Integer.MAX_VALUE)
            if (max != Integer.MAX_VALUE) {
                // /=1000 because it's must in seconds.
                max /= 1000
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, max)

                startActivityForResult(takeVideoIntent, RC_TAKE_VIDEO)

            }else{
                startActivityForResult(takeVideoIntent
                    , RC_TAKE_VIDEO)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== RC_TAKE_VIDEO && resultCode== Activity.RESULT_OK){
            val videoUri =data!!.data
            val retrunIntent=Intent()
            retrunIntent.data=data.data
            setResult(Activity.RESULT_OK,retrunIntent)
            finish()
        }
       else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode== Activity.RESULT_CANCELED){
            finish()
        }


    }

    override fun onBackPressed() {
        finish()
    }


}