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

data class TimeBlockRequest(
    val day: Int,           // Backend convention: 0 = Sunday, 1 = Monday … 6 = Saturday
    val startTime: String,
    val endTime: String
)

data class UserProfileResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val avatar: String?,
    val phone: String?,
    val description: String?,
    val active: Boolean
)

data class StudentSkillResponse(
    val skillId: String,
    val skillName: String
)

data class UpdateAvailabilityRequest(
    val timeBlocks: List<TimeBlockRequest>
)

data class StudentProfileResponse(
    val id: String,
    val user: UserProfileResponse,
    val university: String,
    val career: String,
    val studentId: String,
    val skills: List<StudentSkillResponse>,
    val availability: AvailabilityResponse?,
    val averageRating: Float
)
