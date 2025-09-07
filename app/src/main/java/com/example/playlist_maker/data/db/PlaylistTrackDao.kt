package com.example.playlist_maker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity?

    @Query("SELECT * FROM playlist_tracks")
    suspend fun getAllTracks(): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun deleteTrack(trackId: Long)
}