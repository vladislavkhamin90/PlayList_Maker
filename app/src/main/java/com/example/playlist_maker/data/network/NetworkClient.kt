package com.example.playlist_maker.data.network

import com.example.playlist_maker.data.dto.Response


interface NetworkClient {
    fun doRequest(dto: Any): Response
}