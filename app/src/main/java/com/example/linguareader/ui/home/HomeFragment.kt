package com.example.linguareader.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.MainActivity
import com.example.linguareader.R
import com.example.linguareader.data.BookModel
import com.example.linguareader.data.DeckModel
import com.example.linguareader.databinding.FragmentHomeBinding
import com.example.linguareader.ui.reading.ReadActivity
import com.example.linguareader.StudyActivity
import com.example.linguareader.UpgradeActivity
import com.example.linguareader.UserPreferences

class HomeFragment : Fragment(), HomeRecyclerViewInterface {
    private lateinit var binding: FragmentHomeBinding
    var userPreferences: UserPreferences? = null
    private lateinit var dbHelper: DatabaseHelper

    // Book and deck list
    var bookModelList: ArrayList<BookModel>? = null
    var deckModelList: ArrayList<DeckModel>? = null
    private lateinit var rvBooks: RecyclerView
    private lateinit var rvDecks: RecyclerView
    private lateinit var bookEmpty: View
    private lateinit var deckEmpty: View

    private lateinit var btnUpgrade: Button
    private lateinit var startReadingCard: CardView
    private lateinit var textLastReadLabel: TextView
    private lateinit var textLastReadTitle: TextView
    private lateinit var progressLastRead: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.getRoot()

        // Fetches UserPreferences, which holds data and keys for SharedPreferences
        userPreferences = requireActivity().application as UserPreferences

        // Instantiates the database helper
        dbHelper = DatabaseHelper(context)

        // Gets the BookRecyclerView and sets the content from the database
        rvBooks = root.findViewById(R.id.book_list)
        bookEmpty = root.findViewById(R.id.book_empty)
        refreshBookRecyclerView(dbHelper.books)

        // Adds an onClick to open BooksFragment
        bookEmpty.setOnClickListener {
            findNavController().navigate(
                R.id.action_home_to_books,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, true)
                    .build())
        }

        // Gets the DeckRecyclerView and sets the content from the database
        rvDecks = root.findViewById(R.id.deck_list)
        deckEmpty = root.findViewById(R.id.deck_empty)
        refreshDeckRecyclerView(dbHelper.decks)

        // Adds an onClick to open DecksFragment
        deckEmpty.setOnClickListener {
            findNavController().navigate(
                R.id.action_home_to_decks,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, true)
                    .build())
        }

        // Adds an onClick to start the UpgradeActivity
        btnUpgrade = root.findViewById<View>(R.id.btn_upgrade) as Button
        btnUpgrade.setOnClickListener {
            val intent = Intent(activity, UpgradeActivity::class.java)
            startActivity(intent)
        }

        startReadingCard = root.findViewById<View>(R.id.start_reading_card) as CardView

        textLastReadLabel = root.findViewById<View>(R.id.text_recent_label) as TextView
        textLastReadTitle = root.findViewById<View>(R.id.text_recent_title) as TextView
        progressLastRead = root.findViewById<View>(R.id.last_read_progress) as ProgressBar
        loadSharedPreferences()

        return root
    }

    override fun onResume() {
        super.onResume()
        // Sets the action bar title
        // TODO: display icon
        (activity as MainActivity).supportActionBar!!.setTitle(R.string.title_home)
        (activity as MainActivity).supportActionBar!!.setIcon(R.drawable.ic_home)
    }

    private fun loadSharedPreferences() {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences(UserPreferences.PREFERENCES, Context.MODE_PRIVATE)
        val lastRead: Int = sharedPreferences.getInt(UserPreferences.LAST_READ, -1)

        if (lastRead > -1) {
            val book: BookModel? = dbHelper.getBook(lastRead)
            if (book != null) {
                textLastReadLabel.setText(R.string.continue_reading)
                textLastReadTitle.text = book.title
                progressLastRead.progress = 50

                // Adds an onClick to open the last read book
                startReadingCard.setOnClickListener { // Starts ReadActivity, passing book data as extras
                    val intent = Intent(activity, ReadActivity::class.java)
                    intent.putExtra("TITLE", book.title)
                    intent.putExtra("AUTHOR", book.author)
                    intent.putExtra("FILEPATH", book.filepath)
                    intent.putExtra("LANGUAGE", book.language)
                    startActivity(intent)
                }
            }
        }
        else {
            // Adds an onClick to open BooksFragment
            startReadingCard.setOnClickListener {
                findNavController().navigate(
                    R.id.action_home_to_books,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_home, true)
                        .build())
            }

            progressLastRead.visibility = View.GONE
        }
    }

    override fun onBookClick(pos: Int) {
        val book: BookModel = bookModelList!![pos]

        // Sets the last read book in shared preferences
        userPreferences!!.lastRead = book.id
        val editor: SharedPreferences.Editor = requireContext().getSharedPreferences(
            UserPreferences.PREFERENCES,
            Context.MODE_PRIVATE
        ).edit()
        editor.putInt(UserPreferences.LAST_READ, userPreferences!!.lastRead)
        editor.apply()

        // Starts ReadActivity, passing book data as extras
        val intent = Intent(activity, ReadActivity::class.java)
        intent.putExtra("TITLE", book.title)
        intent.putExtra("AUTHOR", book.author)
        intent.putExtra("FILEPATH", book.filepath)
        intent.putExtra("LANGUAGE", book.language)
        startActivity(intent)
    }

    override fun onDeckClick(pos: Int) {
        val intent = Intent(activity, StudyActivity::class.java)
        intent.putExtra("NAME", deckModelList!![pos].name)
        intent.putExtra("ID", deckModelList!![pos].id)
        startActivity(intent)
    }

    // Populates the LibraryRecyclerView with the list of BookModels
    private fun refreshBookRecyclerView(books: ArrayList<BookModel>) {
        bookModelList = books
        bookEmpty.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        rvBooks.visibility = if (books.isEmpty()) View.GONE else View.VISIBLE

        val bookAdapter = LibraryRecyclerViewAdapter(requireContext(), books, this)
        rvBooks.setAdapter(bookAdapter)
        rvBooks.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
    }

    // Populates the FlashcardsRecyclerView with the list of DeckModels
    private fun refreshDeckRecyclerView(decks: ArrayList<DeckModel>) {
        deckModelList = decks
        deckEmpty.visibility = if (decks.isEmpty()) View.VISIBLE else View.GONE
        rvDecks.visibility = if (decks.isEmpty()) View.GONE else View.VISIBLE

        val deckAdapter = FlashcardsRecyclerViewAdapter(requireContext(), decks, this)
        rvDecks.setAdapter(deckAdapter)
        rvDecks.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
    }
}