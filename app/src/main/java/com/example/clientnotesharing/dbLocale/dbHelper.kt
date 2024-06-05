package com.example.clientnotesharing.dbLocale

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clientnotesharing.data.Annuncio
import kotlinx.serialization.json.Json

class dbHelper(val context: Context): SQLiteOpenHelper(context, DATABASENAME, null, DATABASEVERTION){
    companion object { //cosi mettiamo le costanti qua al posto al di fuori della classe
        //somo inizializzate prima della creazione dell'oggetto
        private val DATABASENAME = "dbExample"
        private val DATABASEVERTION = 1
        private val TABLE_NAME = "UserTable"
        private val ANNUNCIO = "annuncio"
        private val IDANNUNCIO = "id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME (" +
                "$IDANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                "$ANNUNCIO VARCHAR)") //così salviamo l'oggetto annuncio serializzato
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertAnnunci(lista: ArrayList<Annuncio>){
        val db = this.writableDatabase
        for (annuncio in lista){
            val jsonStringAnnuncio = Json.encodeToString(Annuncio.serializer(), annuncio)
            val data = ContentValues()
            data.put(ANNUNCIO, jsonStringAnnuncio)
            data.put(IDANNUNCIO, annuncio.id)
            db.insertWithOnConflict(TABLE_NAME, null, data, SQLiteDatabase.CONFLICT_IGNORE)
        }
        db.close()
    }

    fun getAllData(): ArrayList<Annuncio>{
        var lista = ArrayList<Annuncio>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if(cursor.moveToFirst()){
            do {
                val annuncioJson = cursor.getString(cursor.getColumnIndexOrThrow(ANNUNCIO))
                lista.add(Json.decodeFromString<Annuncio>(annuncioJson))
            }while (cursor.moveToNext())
        }
        return lista
    }

}