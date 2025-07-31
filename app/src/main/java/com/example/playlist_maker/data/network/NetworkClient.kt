package com.example.playlist_maker.data.network

import com.example.playlist_maker.data.dto.Response
import kotlinx.coroutines.flow.Flow

interface NetworkClient {
    suspend fun doRequest(dto: Any): Flow<Response>
}