package com.example.clientnotesharing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.dbLocale.dbHelper
import java.util.Locale
class MyAdapter(private val context: Context) : BaseAdapter(), Filterable {

    private var annunciList: ArrayList<Annuncio> = ArrayList()
    private var filteredAnnunciList: ArrayList<Annuncio> = ArrayList()

    init {
        fetchAnnunciFromDatabase()
    }

    private fun fetchAnnunciFromDatabase() {
        val dbHelper = dbHelper(context)
        annunciList = dbHelper.getAllData()
        filteredAnnunciList = ArrayList(annunciList)
    }

    private class ViewHolder(row: View) {
        val titleTextView: TextView = row.findViewById(R.id.textViewTittle)
        val dateTextView: TextView = row.findViewById(R.id.textViewData)
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

        val annuncio = filteredAnnunciList[position]
        viewHolder.titleTextView.text = annuncio.titolo
        viewHolder.dateTextView.text = annuncio.data

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.lowercase(Locale.ROOT)?.trim()
                val filterResults = FilterResults()

                filterResults.values = if (queryString.isNullOrEmpty())
                    annunciList
                else
                    annunciList.filter {
                        it.titolo.lowercase(Locale.ROOT).contains(queryString)
                    } as ArrayList<Annuncio>
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