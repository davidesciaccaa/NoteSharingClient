package com.example.clientnotesharing.dbLocale

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clientnotesharing.data.Annuncio
import kotlinx.serialization.json.Json

class dbHelper(val context: Context): SQLiteOpenHelper(context, DATABASENAME, null, DATABASEVERSION){
    companion object { //cosi mettiamo le costanti qua al posto al di fuori della classe
        //somo inizializzate prima della creazione dell'oggetto
        private val DATABASENAME = "dbExample"
        private val DATABASEVERSION = 1
        private val TABLE_NAME_ANNUNCIO = "UserTable" //annunci di tutti gli utenti
        private val TABLE_NAME_ANNUNCIO_PERSONALE = "UserPersonalTable" //annunci personali (caricati)
        private val TABLE_NAME_ANNUNCIO_PREFERITO = "UserFavoritesTable" //annunci salvati, preferiti
        private val ANNUNCIO = "annuncio"
        private val IDANNUNCIO = "id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //table per tutti gli annunci
        db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO (" +
                "$IDANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                "$ANNUNCIO VARCHAR)") //così salviamo l'oggetto annuncio serializzato

        //table per gli annunci personali di ciascun utente
        db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO_PERSONALE (" +
                "$IDANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                "$ANNUNCIO VARCHAR)") //così salviamo l'oggetto annuncio serializzato

        //table per gli annunci preferiti di ciascun utente (quelli che salvano tra i preferiti)
                db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO_PREFERITO (" +
                        "$IDANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                        "$ANNUNCIO VARCHAR)") //così salviamo l'oggetto annuncio serializzato
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO_PERSONALE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO_PREFERITO")
        onCreate(db)
    }

    fun insertAnnunci(lista: ArrayList<Annuncio>, tableName: String){
        val db = this.writableDatabase
        for (annuncio in lista){
            val jsonStringAnnuncio = Json.encodeToString(Annuncio.serializer(), annuncio)
            val data = ContentValues()
            data.put(ANNUNCIO, jsonStringAnnuncio)
            data.put(IDANNUNCIO, annuncio.id)
            db.insertWithOnConflict(tableName, null, data, SQLiteDatabase.CONFLICT_IGNORE)
        }
        db.close()
    }

    fun getAllData(tableName: String): ArrayList<Annuncio>{
        var lista = ArrayList<Annuncio>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName", null)
        if(cursor.moveToFirst()){
            do {
                val annuncioJson = cursor.getString(cursor.getColumnIndexOrThrow(ANNUNCIO))
                lista.add(Json.decodeFromString<Annuncio>(annuncioJson))
            }while (cursor.moveToNext())
        }
        return lista
    }
    fun deleteDatabase() {
        context.deleteDatabase(DATABASENAME)
    }

}