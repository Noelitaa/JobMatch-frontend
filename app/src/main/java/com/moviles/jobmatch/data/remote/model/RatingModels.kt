package com.moviles.jobmatch.data.remote.model

data class CreateRatingRequest(
    val idContract: Int,
    val stars: Int,
    val comment: String?
)

data class RatingResponse(
    val idRating: Int,
    val idContract: Int,
    val idRated: String,
    val stars: Int,
    val comment: String?,
    val createdAt: String
)

data class ReceivedRatingResponse(
    val idRating: Int,
    val idContract: Int,
    val raterName: String,
    val jobTitle: String,
    val stars: Int,
    val comment: String?,
    val createdAt: String
)
