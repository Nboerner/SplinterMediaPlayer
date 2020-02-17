package com.example.mtmoon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.audiofx.Visualizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_visualizer_view.*

class VisualizerView : AppCompatActivity() {

    companion object {
        val w : Int = 100
        val h : Int = 100
        var bitmap : Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        private val vizPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xE6329B
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizer_view)
        val canvas = Canvas(bitmap)
        class VisualizerBarView(context: Context, attributes: AttributeSet?) : View(context, attributes) {
            public override fun onDraw(canvas: Canvas) {
                canvas.drawBitmap(bitmap, 0f, 0f, vizPaint)
            }
        }




//        val viz_zone = VisualizerBarView() // need to pass in a canvas I think ?

        Visualizer_Canvas.addView(VisualizerBarView(this, null))

        val visualizer = Visualizer(MainActivity.mp.audioSessionId)
        visualizer.captureSize = Visualizer.getCaptureSizeRange()[0]
        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED)




        println("Pepper Steak")

        visualizer.setDataCaptureListener(object : Visualizer.OnDataCaptureListener{
            override fun onWaveFormDataCapture(
                visualizer: Visualizer?,
                waveform: ByteArray?,
                samplingRate: Int
            ) {
                println("White Tiger Ishida Mitsunari - ") // not reaching this point
                println(waveform.toString())
                for (byte in waveform!!) {
                    println("Buried Well of : $byte")
                }
                // canvas.drawBitmap() ??????????????????////
                bitmap = BitmapFactory.decodeByteArray(waveform, 0, waveform.size)   // is null; cannot be null;  / This is where we will pick up Taimi's game.

            }

            override fun onFftDataCapture(
                visualizer: Visualizer?,
                fft: ByteArray?,
                samplingRate: Int
            ) {
                println("The End is Nigh")
                println("Now.: ${fft.toString()}")
                for (byte in fft!!) {
                    println("Buried Well of : $byte")
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false)

        visualizer.enabled = true


    }




}
