package com.example.linguareader.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.R
import com.example.linguareader.data.DeckModel

class FlashcardsRecyclerViewAdapter(
    var context: Context,
    private var deckList: ArrayList<DeckModel>,
    private val recyclerViewInterface: HomeRecyclerViewInterface
) : RecyclerView.Adapter<FlashcardsRecyclerViewAdapter.ViewHolder?>() {
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.deck_small, parent, false)
        return ViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Sets view data
        holder.textName.text = deckList[position].name
        val cardCount = "x" + deckList[position].size.toString()
        holder.textCards.text = cardCount

        val language = dbHelper.getLanguage(deckList[position].language)!!
        holder.textLanguage.setBackgroundTintList(ColorStateList.valueOf(language.color))
        holder.textLanguage.text = language.code.uppercase()
    }

    override fun getItemCount() = deckList.size


    class ViewHolder(itemView: View, recyclerViewInterface: HomeRecyclerViewInterface?) :
        RecyclerView.ViewHolder(itemView) {
        var textName = itemView.findViewById<TextView>(R.id.text_name)
        var textCards = itemView.findViewById<TextView>(R.id.text_cards)
        var cover = itemView.findViewById<CardView>(R.id.cover)
        var textLanguage = itemView.findViewById<TextView>(R.id.text_language)

        init {
            // Adds a study onClick defined in HomeFragment
            itemView.setOnClickListener {
                if (recyclerViewInterface != null) {
                    val pos: Int = getAdapterPosition()

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onDeckClick(pos)
                    }
                }
            }
        }
    }
}