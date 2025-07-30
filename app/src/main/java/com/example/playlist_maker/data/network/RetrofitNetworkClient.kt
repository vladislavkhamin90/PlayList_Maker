package com.example.playlist_maker.data.network

import com.example.playlist_maker.data.dto.Response
import com.example.playlist_maker.data.dto.TrackSearchRequest
import com.example.playlist_maker.data.dto.TrackSearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class RetrofitNetworkClient(
    private val songsApi: SongsApi
) : NetworkClient {
    override fun doRequest(dto: Any): Flow<Response> = flow {
        try {
            if (dto is TrackSearchRequest) {
                val response = songsApi.search(dto.expression)
                response.resultCode = 200
                emit(response)
            } else {
                emit(Response().apply { resultCode = -1 })
            }
        } catch (e: HttpException) {
            emit(TrackSearchResponse(0, emptyList()).apply {
                resultCode = e.code()
            })
        } catch (e: IOException) {
            emit(Response().apply { resultCode = -1 })
        } catch (e: Exception) {
            emit(Response().apply { resultCode = -2 })
        }
    }
}