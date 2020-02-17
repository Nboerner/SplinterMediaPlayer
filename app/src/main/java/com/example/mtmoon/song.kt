package com.example.mtmoon

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "songDatabase",
    indices = [Index("title")]
)

data class Song (
    @PrimaryKey
    var title: String,

    var idnum: Int,

    var art: Int,

    var idstring: String,

    var localart: ByteArray,

    var length: String
)



