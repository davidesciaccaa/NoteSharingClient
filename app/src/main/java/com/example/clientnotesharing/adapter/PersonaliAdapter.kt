package com.example.clientnotesharing.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MessageResponse
import com.example.clientnotesharing.dbLocale.DbHelper
import kotlinx.coroutines.launch
import retrofit2.Response

/*
 * Questo adapter viene usato nella listView di AnnunciPersonali
 */
class PersonaliAdapter(private val context: Context, private var filteredAnnunciList: ArrayList<Annuncio>) : BaseAdapter() {

    private var annunciList: ArrayList<Annuncio> = ArrayList()
    init {
        annunciList = ArrayList(filteredAnnunciList)
    }
    private class ViewHolder(row: View) {
        val titleTextView: TextView = row.findViewById(R.id.textViewTittle)
        val dateTextView: TextView = row.findViewById(R.id.textViewData)
        val deleteButton: Button = row.findViewById(R.id.btnDelete) // Accessing the button by its ID
    }

    override fun getCount(): Int {
        return filteredAnnunciList.size
    }

    override fun getItem(position: Int): Any {
        return filteredAnnunciList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Metodo per creare o riutilizzare una vista per ogni elemento della lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            // Se convertView è null, viene creata una nuova vista e un nuovo ViewHolder
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_my_annunci, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            // Altrimenti viene riutilizzata la vista esistente e il ViewHolder
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val btnPreferiti = viewHolder.deleteButton
        var annuncio = filteredAnnunciList[position]
        viewHolder.titleTextView.text = annuncio.titolo
        viewHolder.dateTextView.text = annuncio.data

        // Gestione del bottone del cestino
        btnPreferiti.setOnClickListener {
            (context as? LifecycleOwner)?.lifecycleScope?.launch {
                serverEliminaAnnuncio(annuncio, position)
            }

        }
        
        return view
    }

    private suspend fun serverEliminaAnnuncio(annuncio: Annuncio, position: Int) {
        try {
            var result = NotesApi.retrofitService.eliminaAnnuncio(annuncio.id)
            if(result.isSuccessful == true){
                //aggiorno lo stato del db locale
                val db = DbHelper(context)
                db.eliminaAnnuncio(annuncio.id)
                // Remove the item from the list immediately
                filteredAnnunciList.removeAt(position)
                notifyDataSetChanged()
            }else{
                // Da gestire
            }
        } catch (e: Exception) {
            Log.d("TAG", "PersonaliAdapter ${e.printStackTrace()}")
        }
    }

    fun updateData(newList: ArrayList<Annuncio>) {
        annunciList = newList
        filteredAnnunciList = ArrayList(annunciList)
        notifyDataSetChanged()
    }
}
