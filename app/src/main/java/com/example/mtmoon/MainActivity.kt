package com.example.mtmoon

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Bundle
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import androidx.fragment.app.FragmentActivity
import android.view.View
import android.view.View.*
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*



import kotlinx.android.synthetic.main.fragment_good.*

@Suppress("SENSELESS_COMPARISON")
class MainActivity : FragmentActivity() {


    /* Variables accessed by other classes */

    companion object {

        var mp: MediaPlayer = MediaPlayer()

        var currSong: Int = 0

        var totalTime: Int = 0

        var shockTracker: Int = 0

        var activeSong: Song = Song("dummySong", -1, -1, "dummyString", ByteArray(0), "Fiend")

        var uiUpdate: Boolean = false

        var endOfLine: Int = 0

        lateinit var db: MainDatabase

        var needSong: Boolean = false

        var initCheck: Boolean = false

    }

    /* Storage Permission Code is 026 */

    @Suppress("PrivatePropertyName")
    private val MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE: Int = "026".toInt() //// >??


    /* Actions taken when Main Activity is created */

    override fun onCreate(savedInstanceState: Bundle?) {


        setToImmersiveMode()



        if(ContextCompat.checkSelfPermission(this, Manifest.permission
                .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission
                .READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE)
        }


        db = Room.databaseBuilder(applicationContext, MainDatabase::class.java,
            "SongDatabase").build()



        // Database Initialization
        println("RickGameCube, your Time has come.")

        if (db != null) {


            Thread(Runnable {
                println("Your numbers are weak... ${db.roomSongDao().getTitles().size}?")
                for (s in db.roomSongDao().getTitles()) {
                    println("Take that! :" + s.title + s.idnum)
                }
// here


                if (db.roomSongDao().getTitles().isEmpty()) {
                    needSong = true
                    println("We need something in our Database!")
                } else {
                    if (db.roomSongDao().getSongByIdNum(0) != null) {
                        activeSong = db.roomSongDao().getSongByIdNum(0)
                    } else {
                        needSong = true
                    }
                }

            }).start()
        }

// End Databasing


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (activeSong.idnum != -1 && activeSong.idstring != null) { // Database not empty
            println("And our Runner is: $activeSong & ${activeSong.idstring} AND ${activeSong.idnum}")
            initCheck = true
            try {
                mp = MediaPlayer.create(this, Uri.parse(activeSong.idstring))
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, Array(1){Manifest.permission.RECORD_AUDIO}, 48) // request code for Audio Recording is 48
                }
                mp.isLooping = true
                mp.setVolume(0.5f, 0.5f)
                totalTime = mp.duration
                titleText.text = activeSong.title
                if (activeSong.localart.isNotEmpty()) {
                    val localAlbumArt: Bitmap = BitmapFactory.decodeByteArray(
                        activeSong.localart, 0, activeSong.localart.size
                    )
                    albumArt.setImageBitmap(localAlbumArt)
                } else {
                    albumArt.setImageResource(R.drawable.image)
                }

            } catch (e: IllegalStateException) {
                println("Database Corrupted.  Clearing Database.  Please Wait...")
                Thread(Runnable {

                    for (song in db.roomSongDao().getTitles()) {
                        db.roomSongDao().deleteSong(song)
                        println("Corrupted Database has been Purified. Thank you for waiting.")
                    }
                })
                initCheck = false
                titleText.text = R.string.request.toString()
                totalTime = 1
                albumArt.setImageResource(R.drawable.image)
            }
        } else { // Database is empty
            initCheck = false
            titleText.text = R.string.request.toString()
            totalTime = 1
            albumArt.setImageResource(R.drawable.image)
        }

        // Intensity Bar
        intensityBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && initCheck) {
                    val volume = progress / 100.0f
                    mp.setVolume(volume, volume)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        //Position Bar & Scrubber
        positionBar.max = totalTime
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    positionBar.max = totalTime
                    if (fromUser && initCheck) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            }
        )


        //Thread for GUI Updates
        Thread(Runnable {
            @Suppress("SENSELESS_COMPARISON")
            while (!initCheck) {
                //stall
            }

            // First and only run through
            activeSong = db.roomSongDao().getSongByIdNum(0)
            mp = MediaPlayer.create(this, Uri.parse(activeSong.idstring))
            mp.isLooping = true
            mp.setVolume(0.5f, 0.5f)
            totalTime = mp.duration
            while (mp != null) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(500) // update current position every half-second
                } catch (e: InterruptedException) {
                }
                if (uiUpdate) {

                    // update Title Text
                    titleText.text = activeSong.title

                    // update Album Art
                    if (activeSong.localart.isEmpty()) { // Application File
                        albumArt.setImageResource(R.drawable.image)
                    } else { // Local File
                        val localAlbumArt: Bitmap = BitmapFactory.decodeByteArray(
                            activeSong.localart, 0, activeSong.localart.size)
                        albumArt.setImageBitmap(localAlbumArt)
                    }


                    if(mp.isPlaying) {
                        // if music is ongoing, then have pause button showing
                        playButton.setBackgroundResource(R.drawable.pause)
                    }

                    uiUpdate = false // reset flag for UI update
                }

                endOfLine = db.roomSongDao().getTitles().size - 1 // set end number to last index
            }
        }).start()




    }

    override fun onResume() {
        super.onResume()

        setToImmersiveMode()
    }

    private fun setToImmersiveMode() {
        window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or SYSTEM_UI_FLAG_FULLSCREEN
                or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putInt("currSong", currSong)
        outState.putBoolean("uiUpdate", uiUpdate)
    }


    /*
     * Handler for the thread for Position Tracking
     */

    @Suppress("RemoveSingleExpressionStringTemplate")
    private var handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what

            // Update positionBar
            positionBar.progress = currentPosition


            // Update Labels
            val elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = "$elapsedTime"

            val remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }

    }

    /*
     *
     * Creates labels to display elapsed and remaining time for the current song
     *
     */


    fun createTimeLabel(time: Int) : String {
        var timeLabel: String
        val min = time / 1000 / 60
        val sec = time /1000 % 60

        timeLabel = "$min"
        timeLabel += ":"
        if ( sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    /*
     *
     * Function for when the play Button is clicked
     *
     */

    fun playButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {

        if (mp.isPlaying){
            //stop the process
            mp.pause()
            playButton.setBackgroundResource(R.drawable.play)
        } else {
            //start music
            mp.start()
            mp.isLooping = true
            playButton.setBackgroundResource(R.drawable.pause)
        }

    }

    /*
     *
     * Function for when the shock Button is clicked
     *
     */

    fun shockButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        //// shock here
        shockTracker = if (shockTracker == 0) {
            shockButton.setBackgroundResource(R.drawable.shock)
            1
        } else {
            shockButton.setBackgroundResource(R.drawable.noshock)
            0
        }


        val intent = Intent(this, VisualizerView::class.java)
        startActivity(intent)

    }

    /*
     *
     * Skips to the next song in the track list when the next button is clicked
     *
     */

    fun nextButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        //next song
        val goToNext: Boolean = (activeSong.idnum < endOfLine)
            Thread(Runnable {
                activeSong = if (!goToNext) {
                    db.roomSongDao().getSongByIdNum(0)
                } else {
                    db.roomSongDao().getSongByIdNum(activeSong.idnum + 1)
                }
            }).start()

        mp.reset()
        println("Our ID number is: ${activeSong.idnum}")
        println("The End of the Line is this: $endOfLine")
        println("The target data is: ${activeSong.idstring}")
        mp.setDataSource(applicationContext, Uri.parse(
            activeSong.idstring)) // local file

        if (activeSong.localart.isEmpty()) {
            albumArt.setImageResource(R.drawable.image)
        } else {
            val localAlbumArt: Bitmap = BitmapFactory.decodeByteArray(
                activeSong.localart, 0, activeSong.localart.size)
            albumArt.setImageBitmap(localAlbumArt)
        }
        titleText.text = activeSong.title
        mp.prepare()
        mp.isLooping = true
        totalTime = mp.duration
        mp.start()
        playButton.setBackgroundResource(R.drawable.pause)
    }

    /*
     *
     * Goes to the previous song if the song has been playing for < 3 seconds, otherwise it restarts
     *  the song when the previous button is clicked
     *
     */

    fun prevButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        //next song
        Thread(Runnable {
            if (positionBar.progress < 3000) { // < 3 seconds
                activeSong = if (activeSong.idnum != 0) {
                    db.roomSongDao().getSongByIdNum(activeSong.idnum - 1)
                } else {
                    db.roomSongDao().getSongByIdNum(endOfLine)
                }
                println("We moved back!  The current song number is: ${activeSong.idnum}")
            }


        }).start()

        mp.reset()
        mp.setDataSource(applicationContext, Uri.parse(
            activeSong.idstring)) // local file

        if (activeSong.localart.isEmpty()) {
            albumArt.setImageResource(R.drawable.image)
        } else {
            val localAlbumArt: Bitmap = BitmapFactory.decodeByteArray(
                activeSong.localart, 0, activeSong.localart.size)
            albumArt.setImageBitmap(localAlbumArt)
        }
        titleText.text = activeSong.title
        mp.prepare()
        mp.isLooping = true
        totalTime = mp.duration
        mp.start()
        playButton.setBackgroundResource(R.drawable.pause)
    }

    /*
     *
     * Takes the user to the 2nd screen of the application
     *
     */

    fun tracklistButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        val intent = Intent(this, TrackList::class.java)
        startActivity(intent)
    }

    fun bluetoothButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        val intent = Intent(this, Bluetooth::class.java)
        startActivity(intent)
    }



    /** Visualizer **/



}
