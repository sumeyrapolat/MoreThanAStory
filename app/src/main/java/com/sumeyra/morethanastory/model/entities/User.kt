package com.sumeyra.morethanastory.model.entities

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePhotoUrl: String? = null
)
