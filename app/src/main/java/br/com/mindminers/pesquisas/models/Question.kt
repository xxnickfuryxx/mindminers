package br.com.mindminers.pesquisas.models

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val title: String,
    @SerializedName("tempo") val time: String,
    @SerializedName("recompensa") val reward: String
)

data class Choice(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: Question
)