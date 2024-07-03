package com.example.clientnotesharing.dbLocale

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.util.Utility

/*
 * Classe helper per il db locale
 */
class DbHelper(val context: Context): SQLiteOpenHelper(context, DATABASENAME, null, DATABASEVERSION){
    companion object {
        private val DATABASENAME = "dbExample"
        private val DATABASEVERSION = 1
        private val TABLE_NAME_ANNUNCIO = "UserTable"
        private val ID_ANNUNCIO = "id"
        private val TITOLO_ANNUNCIO = "titolo"
        private val DATA_ANNUNCIO = "data"
        private val TIPOMATERIALE_ANNUNCIO = "tipoMateriale"
        private val PROPRIETARIO_ANNUNCIO = "idProprietarioPersona"
        private val AREA_ANNUNCIO = "areaAnnuncio"
        private val PREFERITO_ANNUNCIO = "preferito"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // creo il db
        db?.execSQL("CREATE TABLE $TABLE_NAME_ANNUNCIO (" +
                "$ID_ANNUNCIO VARCHAR PRIMARY KEY,"+ //usare l'id degli annunci
                "$TITOLO_ANNUNCIO VARCHAR,"+
                "$DATA_ANNUNCIO VARCHAR,"+
                "$TIPOMATERIALE_ANNUNCIO INTEGER,"+ //non esiste il tipo boolean
                "$PROPRIETARIO_ANNUNCIO VARCHAR,"+
                "$AREA_ANNUNCIO NUMERIC,"+
                "$PREFERITO_ANNUNCIO INTEGER)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_ANNUNCIO")
        onCreate(db)
    }

    fun deleteDatabase() {
        context.deleteDatabase(DATABASENAME)
    }

    // Metodo per inserire un nuovo annuncio nel db locale
    fun insertAnnunci(lista: ArrayList<Annuncio>){
        val db = this.writableDatabase
        for (annuncio in lista){
            val data = ContentValues()
            data.put(ID_ANNUNCIO, annuncio.id)
            data.put(TITOLO_ANNUNCIO, annuncio.titolo)
            data.put(DATA_ANNUNCIO, annuncio.data)
            data.put(TIPOMATERIALE_ANNUNCIO, if (annuncio.tipoMateriale) 1 else 0)  // Uso un intero perchè sqlite non ha boolean
            data.put(PROPRIETARIO_ANNUNCIO, annuncio.idProprietario)
            data.put(AREA_ANNUNCIO, annuncio.areaAnnuncio)
            data.put(PREFERITO_ANNUNCIO, if (annuncio.preferito) 1 else 0) // Uso un intero perchè sqlite non ha boolean
            db.insertWithOnConflict(TABLE_NAME_ANNUNCIO, null, data, SQLiteDatabase.CONFLICT_IGNORE)
        }
        db.close()
    }

    // Metodo che restituisce una lista con tutti gli annunci nel db locale che non appartengono all'utente loggato
    fun getAllDataEccettoPersonali(): ArrayList<Annuncio>{
        val db = this.readableDatabase
        val username = Utility().getUsername(context)
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_ANNUNCIO WHERE $ID_ANNUNCIO NOT IN (SELECT $ID_ANNUNCIO FROM $TABLE_NAME_ANNUNCIO WHERE $PROPRIETARIO_ANNUNCIO = ?) ;", arrayOf(username))
        return getAnnuncioFromCursor(cursor)
    }

    // Metodo che restituisce tutti gli annunci nel db locale che hanno l'attributo preferito = true
    fun getAnnunciPreferiti(): ArrayList<Annuncio>{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_ANNUNCIO WHERE preferito=1", null) //1 sta per true
        return getAnnuncioFromCursor(cursor)
    }

    // Metodo che aggiorna l'attributo preferito all'annuncio che prende in input, settandolo a true
    fun setPreferiti(annuncio: Annuncio, preferiti: Boolean) {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put(ID_ANNUNCIO, annuncio.id)
        data.put(TITOLO_ANNUNCIO, annuncio.titolo)
        data.put(DATA_ANNUNCIO, annuncio.data)
        data.put(TIPOMATERIALE_ANNUNCIO, if (annuncio.tipoMateriale) 1 else 0)  // Uso un intero perchè sqlite non ha boolean
        data.put(PROPRIETARIO_ANNUNCIO, annuncio.idProprietario)
        data.put(AREA_ANNUNCIO, annuncio.areaAnnuncio)
        data.put(PREFERITO_ANNUNCIO, if (preferiti) 1 else 0)
        db.update(TABLE_NAME_ANNUNCIO, data, "$ID_ANNUNCIO = ?", arrayOf(annuncio.id) )
    }

    // Metodo che elimina l'annuncio corrispondendte all'id che riceve in input
    fun eliminaAnnuncio(id: String) {
        val db = this.readableDatabase
        db.delete(TABLE_NAME_ANNUNCIO, "$ID_ANNUNCIO = ?", arrayOf(id))
    }

    // Metodo che restituisce una lista che contiene tutti gli annunci creati dall'utente che sta usando l'app
    fun getAnnunciPersonali(): ArrayList<Annuncio> {
        val db = this.readableDatabase
        val username = Utility().getUsername(context)
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_ANNUNCIO WHERE $PROPRIETARIO_ANNUNCIO = ?", arrayOf(username))
        return getAnnuncioFromCursor(cursor)
    }

    // Metodo privato per convertire i dati del cursore (ricevuto da una query) in una lista di annunci
    private fun getAnnuncioFromCursor(cursor: Cursor): ArrayList<Annuncio>{
        val lista = ArrayList<Annuncio>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(ID_ANNUNCIO))
                val titolo = cursor.getString(cursor.getColumnIndexOrThrow(TITOLO_ANNUNCIO))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(DATA_ANNUNCIO))
                val tipoM = cursor.getInt(cursor.getColumnIndexOrThrow(TIPOMATERIALE_ANNUNCIO))
                val proprietario = cursor.getString(cursor.getColumnIndexOrThrow(PROPRIETARIO_ANNUNCIO))
                val area = cursor.getInt(cursor.getColumnIndexOrThrow(AREA_ANNUNCIO))
                val preferito = cursor.getInt(cursor.getColumnIndexOrThrow(PREFERITO_ANNUNCIO))
                lista.add(Annuncio(id, titolo, data, if(tipoM==1) true else false, proprietario, area, if(preferito==1) true else false))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return  lista
    }

}