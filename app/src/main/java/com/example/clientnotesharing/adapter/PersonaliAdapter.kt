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
import com.example.clientnotesharing.dbLocale.DbHelper
import kotlinx.coroutines.launch

class PersonaliAdapter(private val context: Context, private var filteredAnnunciList: ArrayList<Annuncio>) : BaseAdapter() {

    private var annunciList: ArrayList<Annuncio> = ArrayList()

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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_my_annunci, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val btnPreferiti = viewHolder.deleteButton
        var annuncio = filteredAnnunciList[position]
        viewHolder.titleTextView.text = annuncio.titolo
        viewHolder.dateTextView.text = annuncio.data

        //il bottone
        btnPreferiti.setOnClickListener {
            (context as? LifecycleOwner)?.lifecycleScope?.launch {
                try {
                    NotesApi.retrofitService.eliminaAnnuncio(annuncio.id)
                    notifyDataSetChanged() // Refresh the list to reflect changes
                } catch (e: Exception) {
                    Log.d("TAG", "PersonaliAdapter ${e.printStackTrace()}")
                }
            }
            //aggiorno lo stato del db locale
            val db = DbHelper(context)
            db.eliminaAnnuncio(annuncio.id)
        }
        
        return view
    }

    fun updateData(newList: ArrayList<Annuncio>) {
        annunciList = newList
        filteredAnnunciList = ArrayList(annunciList)
        notifyDataSetChanged()
    }
}
