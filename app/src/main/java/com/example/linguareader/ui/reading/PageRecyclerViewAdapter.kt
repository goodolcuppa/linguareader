package com.example.linguareader.ui.reading

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.R
import com.example.linguareader.utils.MetricUtils
import com.google.android.flexbox.FlexboxLayoutManager
import java.security.AccessController.getContext



class PageRecyclerViewAdapter(
    var context: Context,
    private var wordList: Array<String>,
    private val recyclerViewInterface: PageRecyclerViewInterface,
    private val textSize: Float
) : RecyclerView.Adapter<PageRecyclerViewAdapter.ViewHolder?>() {
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.text_word, parent, false)
        return ViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set view data
        holder.textWord.text = wordList[position]
        val trimmedWord = wordList[position].replace("[^\\s\\p{L}0-9]".toRegex(), "")
        val wordModel = dbHelper.getWord(trimmedWord)
        if (wordModel != null) {
            when (wordModel.familiarity) {
                0 -> { holder.textWord.setBackgroundResource(R.drawable.bg_word_unknown) }
                1 -> { holder.textWord.setBackgroundResource(R.drawable.bg_word_learning) }
            }
        }
        else {
            holder.textWord.setBackgroundResource(R.drawable.bg_word_unknown)
        }

        val lp = holder.textWord.layoutParams
        val margin = MetricUtils.dpToPx(context, (textSize/4).toInt())
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.setMargins(0, 0, margin, margin)
        }

        val currentNightMode: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> { holder.textWord.setTextColor(context.resources.getColor(R.color.black)) }
            Configuration.UI_MODE_NIGHT_YES -> { holder.textWord.setTextColor(context.resources.getColor(R.color.white)) }
        }
        holder.textWord.textSize = textSize
    }

    override fun getItemCount() = wordList.size

    class ViewHolder(itemView: View, recyclerViewInterface: PageRecyclerViewInterface?) : RecyclerView.ViewHolder(itemView) {
        val textWord = itemView as TextView

        init {
            // Adds a read onClick defined in BooksFragment
            itemView.setOnClickListener {
                if (recyclerViewInterface != null) {
                    val pos: Int = getAdapterPosition()

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos, textWord)
                    }
                }
            }
        }
    }
}
