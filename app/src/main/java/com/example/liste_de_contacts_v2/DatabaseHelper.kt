package com.example.liste_de_contacts_v2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CONTACTS = "contacts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOM = "nom"
        private const val COLUMN_PRENOM = "prenom"
        private const val COLUMN_TELEPHONE = "telephone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_IMAGE_URI = "image_uri"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOM + " TEXT, "
                + COLUMN_PRENOM + " TEXT, "
                + COLUMN_TELEPHONE + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_IMAGE_URI + " TEXT, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL)")
        db.execSQL(createTableQuery)

        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val contacts = listOf(
            Triple("Dupont", "Claire", Triple("06 12 34 56 78", "claire.dupont@example.com", Pair(48.8566, 2.3522))),
            Triple("Martin", "Thomas", Triple("07 23 45 67 89", "thomas.martin@example.com", Pair(45.7640, 4.8357))),
            Triple("Bernard", "Sophie", Triple("06 98 76 54 32", "sophie.bernard@example.com", Pair(43.2965, 5.3698))),
            Triple("Petit", "Lucas", Triple("07 11 22 33 44", "lucas.petit@example.com", Pair(44.8378, -0.5792))),
            Triple("Moreau", "Julien", Triple("06 55 44 33 22", "julien.moreau@example.com", Pair(50.6292, 3.0573)))
        )

        for (c in contacts) {
            val values = ContentValues().apply {
                put(COLUMN_NOM, c.first)
                put(COLUMN_PRENOM, c.second)
                put(COLUMN_TELEPHONE, c.third.first)
                put(COLUMN_EMAIL, c.third.second)
                put(COLUMN_LATITUDE, c.third.third.first)
                put(COLUMN_LONGITUDE, c.third.third.second)
            }
            db.insert(TABLE_CONTACTS, null, values)
        }
    }

    fun getAllContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACTS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM))
                val prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPHONE))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                val lat = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))) null else cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))
                val lng = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))) null else cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))

                contactList.add(Contact(id, nom, prenom, phone, email, imageUri, lat, lng))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contactList
    }

    fun addContact(contact: Contact): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOM, contact.nom)
            put(COLUMN_PRENOM, contact.prenom)
            put(COLUMN_TELEPHONE, contact.telephone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_IMAGE_URI, contact.imageUri)
            put(COLUMN_LATITUDE, contact.latitude)
            put(COLUMN_LONGITUDE, contact.longitude)
        }
        return db.insert(TABLE_CONTACTS, null, values)
    }

    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOM, contact.nom)
            put(COLUMN_PRENOM, contact.prenom)
            put(COLUMN_TELEPHONE, contact.telephone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_IMAGE_URI, contact.imageUri)
            put(COLUMN_LATITUDE, contact.latitude)
            put(COLUMN_LONGITUDE, contact.longitude)
        }
        return db.update(TABLE_CONTACTS, values, "$COLUMN_ID = ?", arrayOf(contact.id.toString()))
    }

    fun deleteContact(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_CONTACTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getContactById(id: Int): Contact? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_CONTACTS, null, "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, null)
        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            val contactId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM))
            val prenom = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRENOM))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPHONE))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            val lat = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))) null else cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))
            val lng = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))) null else cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))

            contact = Contact(contactId, nom, prenom, phone, email, imageUri, lat, lng)
        }
        cursor.close()
        return contact
    }
}
