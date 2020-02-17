package com.example.mtmoon


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE = "songDatabase"

@Database(
    entities = [Song::class],
    version = 1,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {

    abstract fun roomSongDao(): songDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it is open source
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MainDatabase {
            return Room.databaseBuilder(context, MainDatabase::class.java, DATABASE)
                .build()
        }
    }
}