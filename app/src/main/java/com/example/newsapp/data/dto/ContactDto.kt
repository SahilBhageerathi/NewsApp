package com.example.newsapp.data.dto

data class ContactDto(
    val id: String,
    val name: String,
    val phoneNumber: String? = null
)