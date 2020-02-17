package com.example.mtmoon

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_video_viewer.*

class VideoViewer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_viewer)


    }






    fun videoButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        videoButton.setOnClickListener {

            val intent = Intent().setType("video/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,126)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 126) {
                val uri: Uri = Uri.parse(data.data.toString())

                if (uri != Uri.parse("")) {
                    videoView.setVideoURI(uri)
                    val mediaController = MediaController(this)
                    videoView.setMediaController(mediaController)
                    videoView.start()
                }
            }
        }
    }



}
