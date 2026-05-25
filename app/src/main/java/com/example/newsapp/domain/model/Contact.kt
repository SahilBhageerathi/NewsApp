package com.example.newsapp.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String? = null
)