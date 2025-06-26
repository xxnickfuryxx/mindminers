package br.com.mindminers.pesquisas.network

import br.com.mindminers.pesquisas.models.Question
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/questions")
    suspend fun getQuestions(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): List<Question>
}