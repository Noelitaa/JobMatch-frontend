package com.moviles.jobmatch.data.remote.model

data class AddSkillRequest(val skillName: String)

data class AddSkillResponse(
    val skillName: String? = null,
    val message: String? = null
)
