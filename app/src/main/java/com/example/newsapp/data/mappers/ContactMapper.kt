package com.example.newsapp.data.mappers

import com.example.newsapp.data.dto.ContactDto
import com.example.newsapp.domain.model.Contact

fun ContactDto.toDomain() : Contact {
    return Contact(
        id = id,
        name = name,
        phoneNumber = phoneNumber
    )
}