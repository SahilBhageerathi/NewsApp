package com.example.newsapp.data.repo

import android.content.ContentResolver
import android.provider.ContactsContract
import com.example.newsapp.data.dto.ContactDto
import com.example.newsapp.data.mappers.toDomain
import com.example.newsapp.domain.model.Contact
import com.example.newsapp.domain.repo.ContactsRepo

class ContactsRepoImpl(
    private val contentResolver: ContentResolver
) : ContactsRepo {

    override suspend fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,   // no WHERE filter — get all contacts
            null,   // no selection args
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            val seen = mutableSetOf<String>()
            while (cursor.moveToNext()) {
                val contactId = cursor.getString(idIdx)
                if (seen.add(contactId)) {
                    contacts.add(
                        ContactDto(
                            id = contactId,
                            name = cursor.getString(nameIdx) ?: "Unknown",
                            phoneNumber = cursor.getString(phoneIdx)
                        ).toDomain()
                    )
                }
            }
        }

        return contacts
    }
}