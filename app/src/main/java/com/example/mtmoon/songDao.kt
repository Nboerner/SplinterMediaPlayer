package com.example.mtmoon

import androidx.room.*


@Dao
interface songDao {
    @Query("SELECT * FROM songDatabase")
    fun getTitles(): List<Song>

    @Query("SELECT * FROM songDatabase WHERE idnum = :idnumber")
    fun getSongByIdNum(idnumber: Int) : Song

    @Query("SELECT * FROM songDatabase WHERE idstring = :idstr")
    fun getSongByIdString(idstr: String) : Song

    @Delete
    fun deleteSong(song: Song)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSong(song: Song): Long

}
