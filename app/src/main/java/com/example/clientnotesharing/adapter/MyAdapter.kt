package com.example.clientnotesharing.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.dbLocale.dbHelper
import kotlinx.coroutines.launch
import java.util.Locale
class MyAdapter(private val context: Context, private var filteredAnnunciList: ArrayList<Annuncio>) : BaseAdapter(), Filterable {

    private var annunciList: ArrayList<Annuncio> = ArrayList()
    /*
    private var filteredAnnunciList: ArrayList<Annuncio> = ArrayList()

    init {
        fetchAnnunciFromDatabase()
    }

    private fun fetchAnnunciFromDatabase() {
        val dbHelper = dbHelper(context)
        annunciList = dbHelper.getAllData("UserTable")
        filteredAnnunciList = ArrayList(annunciList)
    }
    */

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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.listlayout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val btnPreferiti = viewHolder.favouritesButton
        var annuncio = filteredAnnunciList[position]
        viewHolder.titleTextView.text = annuncio.titolo
        viewHolder.dateTextView.text = annuncio.data
        if(annuncio.preferito){
            //cambio l'icona del btn
            btnPreferiti.setBackgroundResource(R.drawable.favorite_icon)
            notifyDataSetChanged() // Refresh the list to reflect changes
        }
        //il bottone
        btnPreferiti.setOnClickListener {
            //Toast.makeText(context, "Btn clicked", Toast.LENGTH_SHORT).show()
            if(!annuncio.preferito){
                //dico al server di aggiornare l'attributo preferito dell'annuncio
                (context as? LifecycleOwner)?.lifecycleScope?.launch {
                    try {
                        NotesApi.retrofitService.salvaAnnuncioComePreferito(annuncio.id)
                        notifyDataSetChanged() // Refresh the list to reflect changes
                    } catch (e: Exception) {
                        Log.d("TAG", "MyAdapter ${e.printStackTrace()}")
                    }
                }
                //aggiorno lo stato del dblocale
                val db = dbHelper(context)
                db.setPreferiti(annuncio, true)
                Log.d("TAG", "******************Gli annunci preferiti nel db locale sono --> ${db.getAnnunciPreferiti()}")
                Log.d("TAG", "******************Gli annunci nel db locale sono --> ${db.getAllData("UserTable")}")
                for (i in db.provaGetP(annuncio.id)){
                    Log.d("TAG", "******************Preferito 1 =True --> ${i}")
                }
                //for (i in db.getAnnuncioById(annuncio.id)){
                //    Log.d("TAG", "******************Annunciio con id : ${i.id} =True --> ${i}")
                //}

                //aggiorno anche qua lo stato
                annuncio.preferito = true
                //cambio l'icona del btn
                btnPreferiti.setBackgroundResource(R.drawable.favorite_icon)
            }else{
                //dico al server di aggiornare l'attributo preferito dell'annuncio
                (context as? LifecycleOwner)?.lifecycleScope?.launch {
                    try {
                        NotesApi.retrofitService.eliminaAnnuncioComePreferito(annuncio.id)
                        notifyDataSetChanged() // Refresh the list to reflect changes
                    } catch (e: Exception) {
                        Log.d("TAG", "MyAdapter ${e.printStackTrace()}")
                    }
                }
                //aggiorno anche qua lo stato
                annuncio.preferito = false
                //cambio l'icona del btn
                btnPreferiti.setBackgroundResource(R.drawable.heart_plus_icon)
            }

        }
        return view
    }
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

    fun updateData(newList: ArrayList<Annuncio>) {
        annunciList = newList
        filteredAnnunciList = ArrayList(annunciList)
        notifyDataSetChanged()
    }
}