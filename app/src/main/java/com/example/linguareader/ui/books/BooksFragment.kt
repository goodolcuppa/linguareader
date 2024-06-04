package com.example.linguareader.ui.books

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.LoadingActivity
import com.example.linguareader.MainActivity
import com.example.linguareader.R
import com.example.linguareader.RecyclerViewInterface
import com.example.linguareader.UserPreferences
import com.example.linguareader.data.BookModel
import com.example.linguareader.data.LanguageModel
import com.example.linguareader.databinding.FragmentBooksBinding
import com.example.linguareader.utils.MetricUtils
import com.example.linguareader.utils.UriUtils
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.Locale

class BooksFragment : Fragment(), RecyclerViewInterface {
    private lateinit var binding: FragmentBooksBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var dbHelper: DatabaseHelper

    // Book list
    private var bookModelList: ArrayList<BookModel>? = null
    private lateinit var bookRecyclerView: RecyclerView

    // Filter bar
    private lateinit var rgLanguage: RadioGroup
    private lateinit var etFilter: EditText

    // Import dialog
    private lateinit var fab: ExtendedFloatingActionButton
    private lateinit var dialog: Dialog

    private lateinit var btnCancel: Button
    private lateinit var btnImport: Button
    private lateinit var btnSelectFile: ImageButton

    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etLanguage: EditText
    private lateinit var etFilepath: EditText

    // Import data
    private var importedTitle: String? = null
    private var importedAuthor: String? = null
    private var importedFilepath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root
        userPreferences = requireActivity().application as UserPreferences

        // Instantiates the database helper
        dbHelper = DatabaseHelper(context)

        // Gets the RecyclerView and sets the content from the database
        bookRecyclerView = root.findViewById(R.id.rv_books)
        refreshRecyclerView(dbHelper.books)

        // Adds an onClick for the "All" RadioButton
        val rbAll = root.findViewById<RadioButton>(R.id.rb_all)
        rbAll.setOnClickListener { refreshRecyclerView(dbHelper.books) }

        // Creates RadioButtons for the filter bar for all database book languages
        // TODO: Refactor using RecyclerView
        rgLanguage = root.findViewById(R.id.rg_language)
        val bookLanguages: List<LanguageModel> = dbHelper.bookLanguages
        for (language in bookLanguages) {
            val rbLanguage = RadioButton(context)
            rbLanguage.text = language.code.uppercase()
            rbLanguage.setBackgroundResource(R.drawable.rb_language)
            rbLanguage.setButtonDrawable(android.R.color.transparent)
            rbLanguage.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val colorList = ColorStateList.valueOf(language.color)
            if (rbLanguage.isSelected) { rbLanguage.setTextColor(com.google.android.material.R.attr.backgroundColor) }
            else { rbLanguage.setTextColor(colorList) }
            rbLanguage.setBackgroundTintList(colorList)
            rbLanguage.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY)

            val padding: Int = (MetricUtils.dpToPx(requireContext(), 4))
            rbLanguage.setPadding(padding, 0, padding, 0)
            rbLanguage.setHeight((MetricUtils.dpToPx(requireContext(), 28)))
            rbLanguage.setMinWidth((MetricUtils.dpToPx(requireContext(), 40)))
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMarginEnd((MetricUtils.dpToPx(requireContext(), 5)))
            rbLanguage.setLayoutParams(params)

            // Adds an onClick to filter the RecyclerView by language
            rbLanguage.setOnClickListener {
                refreshRecyclerView(
                    dbHelper.getBooks(
                        language.id
                    )
                )
            }

            rbLanguage.setOnCheckedChangeListener { b, selected ->
                if (selected) { rbLanguage.setTextColor(requireContext().resources.getColor(R.color.black)) }
                else { rbLanguage.setTextColor(colorList) }
            }

            rgLanguage.addView(rbLanguage)
        }
        // Hides the languages filter if no books are imported
        if (bookLanguages.isEmpty()) {
            root.findViewById<View>(R.id.language_filter).visibility = View.GONE
        }

        // Adds on onClick to refresh when a term is searched for
        etFilter = root.findViewById(R.id.et_filter)
        etFilter.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    refreshRecyclerView(dbHelper.getBooks(etFilter.getText().toString()))
                    return true
                }
                return false
            }
        })

        // Creates the dialog
        dialog = Dialog(requireContext())

        // Adds an onClick to setup and open the dialog using the FloatingActionButton
        fab = binding.bookFab
        fab.setOnClickListener(View.OnClickListener {
            dialog.setContentView(R.layout.dialog_book)
            dialog.window!!
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            btnCancel = dialog.findViewById(R.id.btn_cancel)
            btnImport = dialog.findViewById(R.id.btn_import)

            etTitle = dialog.findViewById(R.id.et_book_title)
            etAuthor = dialog.findViewById(R.id.et_book_author)
            etLanguage = dialog.findViewById(R.id.et_book_language)
            etFilepath = dialog.findViewById(R.id.et_book_filepath)

            // Adds an onClick to close the dialog
            btnCancel.setOnClickListener { dialog.dismiss() }

            // Adds an onClick to import the book
            btnImport.setOnClickListener(View.OnClickListener { // Checks for title user input
                val title: String = etTitle.getText().toString()
                if (title != "") {
                    importedTitle = title
                }

                // Checks for author user input
                val author: String = etAuthor.getText().toString()
                if (author != "") {
                    importedAuthor = author
                }

                // Checks that the language code is valid
                val languageCode: String = etLanguage.getText().toString()
                val language: LanguageModel =
                    dbHelper.getLanguage(languageCode.lowercase(Locale.getDefault()))
                        ?: return@OnClickListener
                val languageId: Int = language.id

                // Adds the book to the database and informs the user
                val filetype = "txt"
                dbHelper.addBook(
                    BookModel(
                        -1,
                        importedTitle!!,
                        importedAuthor!!,
                        "$importedTitle.$filetype",
                        languageId
                    )
                )
                Toast.makeText(
                    context,
                    "Importing book from '$importedFilepath'.",
                    Toast.LENGTH_SHORT
                ).show()

                // Closes the dialog and refreshes the fragment
                dialog.dismiss()
                requireActivity().supportFragmentManager.beginTransaction().detach(requireParentFragment())
                    .commitNowAllowingStateLoss()
                requireActivity().supportFragmentManager.beginTransaction().attach(requireParentFragment())
                    .commitAllowingStateLoss()
            })

            // Adds an onClick to open a file chooser
            btnSelectFile = dialog.findViewById(R.id.btn_select_file)
            btnSelectFile.setOnClickListener {
                var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.setType("*/*")
                intent = Intent.createChooser(intent, "Choose a file")
                activityResultLauncher.launch(intent)
            }

            // Shows the dialog
            dialog.show()
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        // Sets the action bar title
        // TODO: display icon
        (activity as MainActivity?)!!.supportActionBar!!.title = "Library"
        (activity as MainActivity?)!!.supportActionBar!!.setIcon(R.drawable.ic_books)
    }

    // Adds a listener for when a file is selected
    // TODO: Error handling for unsupported files
    // TODO: Add support for reading PDF and EPUB files
    // TODO: Use ML Kit detect to auto-detect languages
    private var activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result!!.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val uri = data!!.data

                val filename: String = UriUtils.getFilename(requireContext(), uri).split("\\.")[0]

                // Reads the text from the content uri and writes it to a file in internal storage
                if (!UriUtils.writeUriToFile(requireContext(), uri, filename)) {
                    return@registerForActivityResult
                }

                // Sets values for use in the database and for editing by the user
                importedTitle = filename
                etTitle.setHint(importedTitle)
                importedAuthor = "Unknown Author"
                etAuthor.setHint(importedAuthor)
                importedFilepath = filename
                etFilepath.setHint(importedFilepath)

                // Shows the preview layout and enables the import button
                dialog.findViewById<View>(R.id.import_book_details).visibility = View.VISIBLE
                btnImport.isEnabled = true
            }
        }

    // Defines read onClick for the BookRecyclerAdapter
    override fun onItemClick(pos: Int) {
        val book: BookModel = bookModelList!![pos]

        // Sets the last read book in shared preferences
        userPreferences.lastRead = book.id
        val editor: SharedPreferences.Editor = requireContext().getSharedPreferences(
            UserPreferences.PREFERENCES,
            Context.MODE_PRIVATE
        ).edit()
        editor.putInt(UserPreferences.LAST_READ, userPreferences.lastRead)
        editor.apply()

        // Starts ReadActivity, passing book data as extras
        val intent = Intent(activity, LoadingActivity::class.java)
        intent.putExtra("TITLE", book.title)
        intent.putExtra("AUTHOR", book.author)
        intent.putExtra("FILEPATH", book.filepath)
        intent.putExtra("LANGUAGE", book.language)
        startActivity(intent)
    }

    // Defines delete onClick for the BookRecyclerAdapter
    override fun onDeleteClick(pos: Int) {
        // Deletes the book from the database and refreshes the fragment
        dbHelper.deleteBook(bookModelList!![pos].id)
        requireActivity().supportFragmentManager.beginTransaction().detach(requireParentFragment())
            .commitNowAllowingStateLoss()
        requireActivity().supportFragmentManager.beginTransaction().attach(requireParentFragment())
            .commitAllowingStateLoss()
    }

    // Populates the RecyclerView with a specified list of BookModels
    private fun refreshRecyclerView(books: ArrayList<BookModel>) {
        bookModelList = books
        val bookAdapter = BookRecyclerViewAdapter(
            requireContext(),
            books,
            this
        )
        bookRecyclerView.setAdapter(bookAdapter)
        bookRecyclerView.setLayoutManager(LinearLayoutManager(context))
    }
}