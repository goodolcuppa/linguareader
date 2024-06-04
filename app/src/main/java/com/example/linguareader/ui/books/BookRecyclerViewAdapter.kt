package com.example.linguareader.ui.books

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.R
import com.example.linguareader.RecyclerViewInterface
import com.example.linguareader.data.BookModel

class BookRecyclerViewAdapter(
    var context: Context,
    private var bookList: ArrayList<BookModel>,
    private val recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder?>() {
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.book_large, parent, false)
        return ViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set view data
        holder.titleText.text = bookList[position].title
        holder.authorText.text = bookList[position].author
        holder.ratingText.text = bookList[position].rating.toString()

        val color: Int = dbHelper.getLanguage(bookList[position].language)!!.color
        holder.languageTag.setBackgroundColor(color)
    }

    override fun getItemCount() = bookList.size

    class ViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface?) : RecyclerView.ViewHolder(itemView) {
        val titleText = itemView.findViewById<TextView>(R.id.text_title)
        val authorText = itemView.findViewById<TextView>(R.id.text_author)
        val ratingText = itemView.findViewById<TextView>(R.id.text_rating)

        private val btnRead = itemView.findViewById<Button>(R.id.btn_read)
        private val btnDelete = itemView.findViewById<ImageButton>(R.id.btn_delete)

        val languageTag = itemView.findViewById<View>(R.id.language_tag)

        init {
            // Adds a read onClick defined in BooksFragment
            btnRead.setOnClickListener {
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

            // Adds an onClick to delete the book defined in BooksFragment
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
