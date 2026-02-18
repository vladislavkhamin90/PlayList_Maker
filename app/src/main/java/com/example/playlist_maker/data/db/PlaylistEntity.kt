package com.example.playlist_maker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "cover_image_path")
    val coverImagePath: String?,

    @ColumnInfo(name = "track_ids")
    val trackIds: String,

    @ColumnInfo(name = "track_count")
    val trackCount: Int

)