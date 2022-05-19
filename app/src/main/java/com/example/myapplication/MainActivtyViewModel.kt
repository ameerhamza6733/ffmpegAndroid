package com.example.myapplication

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.arthenica.ffmpegkit.FFmpegKit
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class MainActivtyViewModel(application: Application) :AndroidViewModel(application) {
    public var textOnVideo="Soroush Atarod "
    private var inputPatch=""
    private var outPutPath=""

    private val _mutableOutPutVideoFile:MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val outPutVideoLiveData : LiveData<Event<Resource<String>>> =  _mutableOutPutVideoFile


    fun addTextToVideo(){

        FFmpegKit.executeAsync("-i $inputPatch -vf \"drawtext=fontfile=/system/fonts/DroidSans.ttf:text='$textOnVideo':fontcolor=white:fontsize=50:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2\" -codec:a copy -y $outPutPath",
            { session ->
                val state = session.state
                val returnCode = session.returnCode

                // CALLED WHEN SESSION IS EXECUTED
                _mutableOutPutVideoFile.postValue(Event(Resource.Success(outPutPath)))
                log(
                    String.format(
                        "FFmpeg process exited with state %s and rc %s.%s",
                        state,
                        returnCode,
                        session.failStackTrace
                    )
                )
            }, {
                // CALLED WHEN SESSION PRINTS LOGS
                log(it.message)
            }) {
            log(it.sessionId.toString())
            // CALLED WHEN SESSION GENERATES STATISTICS
        }
    }

    fun copyOrignalFileToTempFile(orinalFileUri:Uri){
        _mutableOutPutVideoFile.postValue(Event(Resource.Loading()))
       viewModelScope.launch (Dispatchers.IO){
           val applicationContext=getApplication<Application>().applicationContext
           val inputStream= applicationContext.contentResolver.openInputStream(orinalFileUri)
           val file=copyVideoStreamToVideoFile(inputStream)
           inputPatch=file?.absolutePath.toString()
           log(file!!.absolutePath)
           outPutPath = applicationContext.createVideoFile(".mp4")?.absolutePath.toString()

           addTextToVideo()
       }
    }

   private  fun copyVideoStreamToVideoFile(inputStream: InputStream?): File? {
        val applicationContext=getApplication<Application>().applicationContext
        val outFile = applicationContext.createVideoFile(".mp4")
        outFile?.copyInputStreamToFile(inputStream)
        return outFile
    }
}