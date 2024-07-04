package com.example.clientnotesharing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.dbLocale.DbHelper
import java.util.Locale

/*
 * Questo adapter viene usato nella listView della HomeScreen e anche della schermata Preferiti
 */
class MyAdapter(private val context: Context, private var filteredAnnunciList: ArrayList<Annuncio>) : BaseAdapter(), Filterable {

    private var annunciList: ArrayList<Annuncio> = ArrayList()

    init {
        annunciList = ArrayList(filteredAnnunciList)
    }

    private class ViewHolder(row: View) {
        val titleTextView: TextView = row.findViewById(R.id.textViewTittle)
        val dateTextView: TextView = row.findViewById(R.id.textViewData)
        val favouritesButton: Button = row.findViewById(R.id.buttonFavourite) // Accessing the button by its ID

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
            view = inflater.inflate(R.layout.listlayout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            // Altrimenti viene riutilizzata la vista esistente e il ViewHolder
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val btnPreferiti = viewHolder.favouritesButton
        var annuncio = filteredAnnunciList[position]
        viewHolder.titleTextView.text = annuncio.titolo
        viewHolder.dateTextView.text = annuncio.data

        //Gestione del btn preferiti
        // Controllo se l'annuncio è preferito dal db locale, quando viene fatto il refresh viene aggiornato il db locale
        val db = DbHelper(context)
        if(db.isAnnuncioPreferito(annuncio.id)){
            //cambio l'icona del btn
            btnPreferiti.setBackgroundResource(R.drawable.favorite_icon)
            notifyDataSetChanged() // Refresh the list to reflect changes
        }
        //il bottone dei preferiti (il cuore)
        btnPreferiti.setOnClickListener {
            //Toast.makeText(context, "Btn clicked", Toast.LENGTH_SHORT).show()
            if(!db.isAnnuncioPreferito(annuncio.id)){
                //dico al server di aggiornare l'attributo preferito dell'annuncio
                setPreferitoBtn(annuncio, btnPreferiti)
            }else{
                //dico al server di aggiornare l'attributo preferito dell'annuncio
                setNotPreferitoBtn(annuncio, btnPreferiti)


            }

        }
        return view
    }

    // Modifica l'attributo preferito dell'annuncio (mettendolo a false) e modifica l'icona del heart button
    private fun setNotPreferitoBtn(annuncio: Annuncio, btnPreferiti: Button) {
        // elimino anche dal db locale
        val db = DbHelper(context)
        db.setPreferiti(annuncio, false)
        //cambio l'icona del btn
        btnPreferiti.setBackgroundResource(R.drawable.heart_plus_icon)
    }

    // Modifica l'attributo preferito dell'annuncio (mettendolo a true) e modifica l'icona del heart button
    private fun setPreferitoBtn(annuncio: Annuncio, btnPreferiti: Button) {
        //aggiorno lo stato del dblocale
        val db = DbHelper(context)
        db.setPreferiti(annuncio, true)
        //aggiorno anche qua lo stato
        //annuncio.preferito = true
        //cambio l'icona del btn
        btnPreferiti.setBackgroundResource(R.drawable.favorite_icon)

    }

    // Implementazione del filtro per la ricerca degli annunci
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.lowercase(Locale.ROOT)?.trim()
                val filterResults = FilterResults()
                filterResults.values = if (queryString.isNullOrEmpty()) {
                    annunciList
                } else {
                    val isNumber = queryString.toIntOrNull()
                    if (isNumber != null && isNumber in 0..4) {
                        annunciList.filter { it.areaAnnuncio == isNumber } as ArrayList<Annuncio>
                    } else {
                        annunciList.filter { it.titolo.lowercase(Locale.ROOT).contains(queryString) } as ArrayList<Annuncio>
                    }
                }
                return filterResults
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredAnnunciList = results?.values as ArrayList<Annuncio>
                notifyDataSetChanged()
            }
        }
    }

    // Metodo per aggiornare i dati della lista
    fun updateData(newList: ArrayList<Annuncio>) {
        annunciList = newList
        filteredAnnunciList = ArrayList(annunciList)
        notifyDataSetChanged()
    }

}
