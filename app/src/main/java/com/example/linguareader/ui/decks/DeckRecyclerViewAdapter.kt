package com.example.linguareader.ui.decks

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.R
import com.example.linguareader.RecyclerViewInterface
import com.example.linguareader.data.DeckModel

class DeckRecyclerViewAdapter(
    var context: Context,
    private var deckList: ArrayList<DeckModel>,
    private val recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<DeckRecyclerViewAdapter.ViewHolder?>() {
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.deck_large, parent, false)
        return ViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set view data
        holder.textName.text = deckList[position].name
        val cardCount = "x" + deckList[position].size.toString()
        holder.textCards.text = cardCount

        val language = dbHelper.getLanguage(deckList[position].language)!!
        holder.textLanguage.setBackgroundTintList(ColorStateList.valueOf(language.color))
        holder.textLanguage.text = language.code.uppercase()
    }

    override fun getItemCount() = deckList.size

    class ViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface?) :
        RecyclerView.ViewHolder(itemView) {
        var textName = itemView.findViewById<TextView>(R.id.text_name)
        var textCards = itemView.findViewById<TextView>(R.id.text_cards)
        var textLanguage = itemView.findViewById<TextView>(R.id.text_language)

        private var btnDelete = itemView.findViewById<ImageButton>(R.id.btn_delete_deck)

        init {
            // Adds a study onClick defined in DecksFragment
            itemView.setOnClickListener {
                if (recyclerViewInterface != null) {
                    val pos: Int = getAdapterPosition()

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos)
                    }
                }
            }

            // Adds an onLongClick to show the delete button
            itemView.setOnLongClickListener {
                btnDelete.setVisibility(View.VISIBLE)
                true
            }

            //Adds an onClick to delete the deck defined in DecksFragment
            btnDelete.setOnClickListener {
                if (recyclerViewInterface != null) {
                    val pos: Int = getAdapterPosition()

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onDeleteClick(pos)
                    }
                }
            }
        }
    }
}