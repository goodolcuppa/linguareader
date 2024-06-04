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
import com.example.linguareader.data.BookModel

class LibraryRecyclerViewAdapter(
    var context: Context,
    private var bookList: ArrayList<BookModel>,
    private val recyclerViewInterface: HomeRecyclerViewInterface
) : RecyclerView.Adapter<LibraryRecyclerViewAdapter.ViewHolder?>() {
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.book_small, parent, false)
        return ViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Sets view data
        holder.textTitle.text = bookList[position].title
        holder.textAuthor.text = bookList[position].author

        val language = dbHelper.getLanguage(bookList[position].language)!!
        holder.textLanguage.setBackgroundTintList(ColorStateList.valueOf(language.color))
        holder.textLanguage.text = language.code.uppercase()
    }

    override fun getItemCount() = bookList.size

    class ViewHolder(itemView: View, recyclerViewInterface: HomeRecyclerViewInterface?) :
        RecyclerView.ViewHolder(itemView) {
        var textTitle = itemView.findViewById<TextView>(R.id.text_title)
        var textAuthor = itemView.findViewById<TextView>(R.id.text_author)
        var cover = itemView.findViewById<CardView>(R.id.cover)
        var textLanguage = itemView.findViewById<TextView>(R.id.text_language)

        init {
            // Adds a read onClick defined in HomeFragment
            itemView.setOnClickListener {
                if (recyclerViewInterface != null) {
                    val pos: Int = getAdapterPosition()

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onBookClick(pos)
                    }
                }
            }
        }
    }
}
