package com.example.mtmoon

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_track_list.*
import kotlinx.android.synthetic.main.activity_track_list.loadButton
import com.example.mtmoon.MainActivity.Companion.currSong
import com.example.mtmoon.MainActivity.Companion.mp
import com.example.mtmoon.MainActivity.Companion.totalTime
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import androidx.core.view.isInvisible
import kotlinx.android.synthetic.main.song_switch.view.*
import java.lang.Thread.sleep


@Suppress("UNUSED_PARAMETER")
class TrackList : AppCompatActivity() {


    private var addSongFlag : Boolean = false

    private var leftColumn: Boolean = true

    private var isDuplicate: Boolean = false

    private var trackNumber: Int = 0

    private lateinit var targetSong: Song

    private lateinit var songNum: String


    private val db = MainActivity.db

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_list)
        populateListThread()

        setToImmersiveMode()
    }

    override fun onResume() {
        super.onResume()

        setToImmersiveMode()
    }

    private fun setToImmersiveMode() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }


    fun loadButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        loadButton.setOnClickListener {

            val intent = Intent().setType("*/*").setAction(Intent.ACTION_OPEN_DOCUMENT)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            startActivityForResult(Intent.createChooser(intent, "Select an .mp3 file"),
                126)
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 126 && resultCode == FragmentActivity.RESULT_OK) {
            val selectedmp3 = data?.data // URI w/ location of mp3
            contentResolver.takePersistableUriPermission(selectedmp3!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            println(contentResolver.persistedUriPermissions)
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(this, selectedmp3)

            var songtitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

            val altSongTitle = data.toString()
            println("Alternate Title is: $altSongTitle")
            println("Our Index is: ${altSongTitle.lastIndexOf('/')} and our length is ${altSongTitle.length}")
            val index1 = altSongTitle.lastIndexOf('/')
            val index2 = altSongTitle.lastIndexOf('.')
            val songduration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val songalbumart: ByteArray
            songalbumart = if (mmr.embeddedPicture != null) {
                mmr.embeddedPicture
            } else {
                ByteArray(0)
            }

            if (songtitle == null) {
                println("Alternate Title is: $altSongTitle")
                println("Index 1 is: $index1 and Index 2 is: $index2")
                songtitle = altSongTitle.substring(index1 + 1, index2)
            }
            val tempSong = Song(
                songtitle, trackNumber, R.drawable.image,
                Uri.parse(selectedmp3.toString()).toString(), songalbumart, songduration

            )
            //
            //
            // TrackListHeader.text = trackNumber.toString() // why is this here, Noah?


            addSongFlag = true

            Thread(Runnable {
                if (addSongFlag) {
                    val insert = db.roomSongDao().insertSong(tempSong)
                    println("Our Muddy Waters are equal to: $insert")
                    if (insert == -1L) {
                        addSongFlag = false
                        println("Red Sun Over Paradise")
                        isDuplicate = true
                    } else {


                        println(
                            "RickGameCube, we may be on to something! : "
                                    + db.roomSongDao().getTitles()
                        )
                        for (i in db.roomSongDao().getTitles()) {
                            println("take that! + " + i.title + i.idnum)
                        }
                        addSongFlag = false
                    }


                    this@TrackList.runOnUiThread {


                        println("Who are you to defy our traditions?")


                        if (isDuplicate) {
                            isDuplicate = false
                            println("End of Function.  Goodbye.")
                        } else {


                            val newSong = Button(this)
                            createButton(
                                newSong,
                                songtitle,
                                trackNumber,
                                tempSong,
                                songduration
                            )


                            println("Yeah we bein' here")


                            trackNumber++
                            TrackListHeader.text = trackNumber.toString()
                        }


                        if (!MainActivity.initCheck) {
                            MainActivity.initCheck = true
                            MainActivity.uiUpdate = true
                        }
                    }

                }
            }).start()
        }
    }


    // 若宮公園










    @SuppressLint("InflateParams")
    private fun createButton(button: Button, info: String, tracknumber: Int, song: Song, duration: String): Button {


        val inflater = LayoutInflater.from(applicationContext)

        val vg = inflater.inflate(R.layout.song_switch,null)

        val albumbutton = vg.albumButton


            println(vg.overViewFrame.visibility)


        var timeLabel: String
        val min = duration.toInt() / 1000 / 60
        val sec = duration.toInt() /1000 % 60

        timeLabel = "$min"
        timeLabel += ":"
        if(sec < 10) {
            timeLabel += "0"
        }
        timeLabel += "$sec"



        vg.overViewFrame.nameplate.text = info
        vg.overViewFrame.timeplate.text = timeLabel

        println("$info + $timeLabel")
        if (song.localart.isNotEmpty()) {
            val localAlbumArt: Bitmap = BitmapFactory.decodeByteArray(
                song.localart, 0, song.localart.size
            )
            val aaBitmap = BitmapDrawable(resources, localAlbumArt)

            vg.albumButton.background = aaBitmap
        } else {
            vg.albumButton.setBackgroundResource(R.drawable.image)
        }

        vg.setPadding(20, 40, 20, 40)



        if (vg.timeplate.isInvisible) {
            println("Ruptured Album Heart ")
        }

        albumbutton.id = tracknumber
        albumbutton.tag = tracknumber
        albumbutton.setOnClickListener {
            songButtonClick(albumbutton)
        }

        albumbutton.visibility = View.VISIBLE

        leftColumn = if (leftColumn) {
            LeftColumn.addView(vg)
            false
        } else {
            RightColumn.addView(vg)
            true
        }

        return albumbutton
    }









    private fun songButtonClick(v: View) {

        songNum = v.tag.toString()
        currSong = songNum.toInt()
        println("The current song number is: $currSong")




        Thread(Runnable {

            mp.reset()
            println("Id is: " + v.id)
            targetSong = db.roomSongDao().getSongByIdNum(v.id)
            println("The target data is: ${targetSong.idstring}")
            mp.setDataSource(applicationContext, Uri.parse(targetSong.idstring))
            mp.prepare()
            mp.isLooping = true
            totalTime = mp.duration
            mp.start()
            MainActivity.activeSong = targetSong
            MainActivity.uiUpdate = true


        }).start()

    }














    /*
     *
     * Populates the Track List with the 3 default songs
     *
     */

    private fun populateListThread() {

        Thread(Runnable {
            sleep(500)
            var i = 0
            trackNumber = db.roomSongDao().getTitles().size
            for (s in db.roomSongDao().getTitles()) {
                this@TrackList.runOnUiThread {
                    val newSong = Button(this)
                    val title = s.title
                    s.idnum = i
                    val newButton = createButton(newSong, title, i, s, s.length)
                    newButton.id = i
                    newButton.tag = i
                    i++

                    TrackListHeader.text = trackNumber.toString()

                }
            }
            println("Stuart Little, we may be on to something! : "
                    + db.roomSongDao().getTitles())
            for (ac in db.roomSongDao().getTitles()) {
                println("take that! + " + ac.title + ac.idnum)
            }
        }).start()

    }


    fun clearButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        Thread(Runnable {
            for(s in db.roomSongDao().getTitles()) {
                db.roomSongDao().deleteSong(s)
            }

            trackNumber = db.roomSongDao().getTitles().size
        }).start()
        TrackListHeader.text = "0"

        LeftColumn.removeAllViews() // clear UI Buttons
        RightColumn.removeAllViews() // clear UI Buttons
        leftColumn = true

    }

}
