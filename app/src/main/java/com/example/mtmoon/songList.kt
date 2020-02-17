package com.example.mtmoon
//
//import androidx.room.Room
//
//object SongList {
//
//
//    var trackList : MutableList<Song> = ArrayList()
//    var tracklistSize: Int = 2
//    private var nameList = listOf("Club Serene", "Hit the Floor",
//        "Twilight of the Gods")
//    private var idList = listOf(R.raw.club_serene, R.raw.hit_the_floor,
//        R.raw.twilight_of_the_gods)
//    private var artList = listOf(R.drawable.ryuu_ga_gotoku_aa, R.drawable.katana_zero_aa,
//        R.drawable.shadows_of_valentia_aa)
//
//     fun generateTracklist() {
//        for (i in nameList.indices) {
//            val temp = Song(nameList[i], idList[i], artList[i], "", ByteArray(0))
//            trackList.add(i, temp)
//        }
//    }
//
//    @Suppress("unused")
//    fun addSong(name: String, id: Int, art: Int) {
//        val temp = Song(name, id, art, "", ByteArray(0))
//        tracklistSize++
//        trackList.add(temp)
//    }
//
//    fun addSongFromDevice(name: String, idnum: Int, idstring: String, art: ByteArray) : Song {
//        val temp = Song(name, idnum, R.drawable.image, idstring, art)
////        tracklistSize++
////        trackList.add(tracklistSize, temp)
//        return temp
//
////
////        libDao.insertSong(temp)
////
////        println(db.toString())
//
//    }
//}