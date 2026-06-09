package com.moviles.jobmatch.data.remote.model

data class TimeSlot(
    val start: String,
    val end: String
)

data class AvailabilityResponse(
    val monday: List<TimeSlot> = emptyList(),
    val tuesday: List<TimeSlot> = emptyList(),
    val wednesday: List<TimeSlot> = emptyList(),
    val thursday: List<TimeSlot> = emptyList(),
    val friday: List<TimeSlot> = emptyList(),
    val saturday: List<TimeSlot> = emptyList(),
    val sunday: List<TimeSlot> = emptyList()
)

data class UserProfileResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val avatar: String?,
    val phone: String?,
    val bio: String?,
    val active: Boolean
)

data class StudentProfileResponse(
    val id: String,
    val user: UserProfileResponse,
    val university: String,
    val career: String,
    val studentId: String,
    val skills: List<String>,
    val availability: AvailabilityResponse?,
    val averageRating: Float
)
