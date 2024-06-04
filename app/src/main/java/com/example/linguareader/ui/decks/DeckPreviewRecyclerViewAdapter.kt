package com.example.linguareader.ui.decks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.R
import com.example.linguareader.data.WordModel

class DeckPreviewRecyclerViewAdapter(
    var context: Context,
    private var cardList: ArrayList<WordModel>,
) : RecyclerView.Adapter<DeckPreviewRecyclerViewAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.deck_preview_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set view data
        holder.textWord.text = cardList[position].word
        holder.textTranslation.text = cardList[position].translation
    }

    override fun getItemCount() = cardList.size

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textWord = itemView.findViewById<TextView>(R.id.text_word)
        var textTranslation = itemView.findViewById<TextView>(R.id.text_translation)
    }
}