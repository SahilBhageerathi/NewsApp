package com.example.newsapp.domain.repo

import com.example.newsapp.domain.model.Contact

interface ContactsRepo {
    suspend fun getContacts(): List<Contact>
}