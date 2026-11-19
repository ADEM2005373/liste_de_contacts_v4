package com.example.liste_de_contacts_v2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper(application)
    
    private val _contacts = MutableLiveData<MutableList<Contact>>()
    val contacts: LiveData<MutableList<Contact>> = _contacts

    init {
        loadContacts()
    }

    fun loadContacts() {
        _contacts.value = dbHelper.getAllContacts().toMutableList()
    }

    fun addContact(contact: Contact) {
        dbHelper.addContact(contact)
        loadContacts()
    }

    fun updateContact(contact: Contact) {
        dbHelper.updateContact(contact)
        loadContacts()
    }

    fun deleteContact(id: Int) {
        dbHelper.deleteContact(id)
        loadContacts()
    }
    
    fun getContactById(id: Int): Contact? {
        return dbHelper.getContactById(id)
    }
    
    fun getNextId(): Int {
        val list = dbHelper.getAllContacts()
        return (list.maxOfOrNull { it.id } ?: 0) + 1
    }
}
