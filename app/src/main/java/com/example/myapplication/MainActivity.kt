package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import dev.sagar.lifescience.utils.Resource
import java.io.File
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private val RC_PICK_VIDEO=32

    private lateinit var videoView:VideoView
    private lateinit var process: ProgressBar

    private val viewMode : MainActivtyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btRecodeVideo =findViewById<Button>(R.id.btReccodeVideo)
        videoView=findViewById(R.id.video_view)
        process=findViewById(R.id.progress_circular)

        btRecodeVideo.setOnClickListener {
            val intent=Intent(this,TakeVideoPhotoFromCameraActivity::class.java)
            intent.setAction(TakeVideoPhotoFromCameraActivity.ACTION_TAKE_VIDEO)
            startActivityForResult(intent,RC_PICK_VIDEO)
        }

        initObserver()
    }

    private fun initObserver(){
        viewMode.outPutVideoLiveData.observe(this,{
            it.getContentIfNotHandled()?.let { it2->
                when(it2){
                    is Resource.Success->{
                        videoView.visibility=View.VISIBLE
                        process.visibility= View.INVISIBLE
                        videoView.setVideoPath(it2.response)
                        videoView.start()
                    }
                    is Resource.Loading->{
                        videoView.visibility=View.INVISIBLE
                        process.visibility= View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RC_PICK_VIDEO==requestCode && resultCode ==Activity.RESULT_OK){
            viewMode.copyOrignalFileToTempFile(data!!.data!!)
        }
    }



}