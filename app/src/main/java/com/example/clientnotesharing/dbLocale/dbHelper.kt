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
        //private val TABLE_NAME_ANNUNCIO_PREFERITO = "UserFavoritesTable" //annunci salvati, preferiti
        private val ID_ANNUNCIO = "id"
        private val TITOLO_ANNUNCIO = "titolo"
        private val DATA_ANNUNCIO = "data"
        private val DESC_ANNUNCIO = "descrizioneAnnuncio"
        private val TIPOMATERIALE_ANNUNCIO = "tipoMateriale"
        private val PROPRIETARIO_ANNUNCIO = "idProprietarioPersona"
        private val AREA_ANNUNCIO = "areaAnnuncio"
        private val PREFERITO_ANNUNCIO = "preferito"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //table per tutti gli annunci
        db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO (" +
                "$ID_ANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                "$TITOLO_ANNUNCIO VARCHAR,"+
                "$DATA_ANNUNCIO VARCHAR,"+
                "$DESC_ANNUNCIO VARCHAR,"+
                "$TIPOMATERIALE_ANNUNCIO INTEGER,"+ //non esiste il topo boolean
                "$PROPRIETARIO_ANNUNCIO VARCHAR,"+
                "$AREA_ANNUNCIO NUMERIC,"+
                "$PREFERITO_ANNUNCIO INTEGER)")

        //table per gli annunci personali di ciascun utente
        db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO_PERSONALE (" +
                "$ID_ANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                "$TITOLO_ANNUNCIO VARCHAR,"+
                "$DATA_ANNUNCIO VARCHAR,"+
                "$DESC_ANNUNCIO VARCHAR,"+
                "$TIPOMATERIALE_ANNUNCIO INTEGER,"+ //non esiste il topo boolean
                "$PROPRIETARIO_ANNUNCIO VARCHAR,"+
                "$AREA_ANNUNCIO NUMERIC,"+
                "$PREFERITO_ANNUNCIO INTEGER)")

        /*//table per gli annunci preferiti di ciascun utente (quelli che salvano tra i preferiti)
                db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO_PREFERITO (" +
                        "$IDANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                        "$ANNUNCIO VARCHAR)") //così salviamo l'oggetto annuncio serializzato

         */
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO_PERSONALE")
        //db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO_PREFERITO")
        onCreate(db)
    }

    fun insertAnnunci(lista: ArrayList<Annuncio>, tableName: String){
        val db = this.writableDatabase
        for (annuncio in lista){
            //val jsonStringAnnuncio = Json.encodeToString(Annuncio.serializer(), annuncio)
            val data = ContentValues()
            data.put(ID_ANNUNCIO, annuncio.id)
            data.put(TITOLO_ANNUNCIO, annuncio.titolo)
            data.put(DATA_ANNUNCIO, annuncio.data)
            data.put(DESC_ANNUNCIO, annuncio.descrizioneAnnuncio)
            data.put(TIPOMATERIALE_ANNUNCIO, if (annuncio.tipoMateriale) 1 else 0)  // Convert Boolean to Integer
            data.put(PROPRIETARIO_ANNUNCIO, annuncio.idProprietario)
            data.put(AREA_ANNUNCIO, annuncio.areaAnnuncio)
            data.put(PREFERITO_ANNUNCIO, if (annuncio.preferito) 1 else 0)  // Convert Boolean to Integer
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
                val id = cursor.getString(cursor.getColumnIndexOrThrow(ID_ANNUNCIO))
                val titolo = cursor.getString(cursor.getColumnIndexOrThrow(TITOLO_ANNUNCIO))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(DATA_ANNUNCIO))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(DESC_ANNUNCIO))
                val tipoM = cursor.getInt(cursor.getColumnIndexOrThrow(TIPOMATERIALE_ANNUNCIO))
                val proprietario = cursor.getString(cursor.getColumnIndexOrThrow(PROPRIETARIO_ANNUNCIO))
                val area = cursor.getInt(cursor.getColumnIndexOrThrow(AREA_ANNUNCIO))
                val preferito = cursor.getInt(cursor.getColumnIndexOrThrow(PREFERITO_ANNUNCIO))
                lista.add(Annuncio(id, titolo, data, desc, if(tipoM==1) true else false, proprietario, area, if(preferito==1) true else false))
            }while (cursor.moveToNext())
        }
        return lista
    }

    fun getAnnunciPreferiti(): ArrayList<Annuncio>{
        var lista = ArrayList<Annuncio>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_ANNUNCIO WHERE preferito=1", null) //1 sta per true
        if(cursor.moveToFirst()){
            do {
                do {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(ID_ANNUNCIO))
                    val titolo = cursor.getString(cursor.getColumnIndexOrThrow(TITOLO_ANNUNCIO))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(DATA_ANNUNCIO))
                    val desc = cursor.getString(cursor.getColumnIndexOrThrow(DESC_ANNUNCIO))
                    val tipoM = cursor.getInt(cursor.getColumnIndexOrThrow(TIPOMATERIALE_ANNUNCIO))
                    val proprietario = cursor.getString(cursor.getColumnIndexOrThrow(PROPRIETARIO_ANNUNCIO))
                    val area = cursor.getInt(cursor.getColumnIndexOrThrow(AREA_ANNUNCIO))
                    val preferito = cursor.getInt(cursor.getColumnIndexOrThrow(PREFERITO_ANNUNCIO))
                    lista.add(Annuncio(id, titolo, data, desc, if(tipoM==1) true else false, proprietario, area, if(preferito==1) true else false))
                }while (cursor.moveToNext())
            }while (cursor.moveToNext())
        }
        return lista
    }
    fun deleteDatabase() {
        context.deleteDatabase(DATABASENAME)
    }
    fun setPreferiti(idA: String, preferiti: Boolean) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(PREFERITO_ANNUNCIO, if (preferiti) 1 else 0)  // Convert Boolean to Integer
        }
        db.update(TABLE_NAME_ANNUNCIO_PERSONALE, contentValues, "$ID_ANNUNCIO = ?", arrayOf(idA))
    }

}