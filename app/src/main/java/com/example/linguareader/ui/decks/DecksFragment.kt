package com.example.linguareader.ui.decks

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguareader.DatabaseHelper
import com.example.linguareader.MainActivity
import com.example.linguareader.R
import com.example.linguareader.RecyclerViewInterface
import com.example.linguareader.StudyActivity
import com.example.linguareader.data.DeckModel
import com.example.linguareader.data.LanguageModel
import com.example.linguareader.data.WordModel
import com.example.linguareader.databinding.FragmentDecksBinding
import com.example.linguareader.utils.MetricUtils
import com.example.linguareader.utils.UriUtils
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.Locale

class DecksFragment : Fragment(), RecyclerViewInterface {
    private lateinit var binding: FragmentDecksBinding
    private lateinit var dbHelper: DatabaseHelper

    // Deck list
    private var deckModelList: ArrayList<DeckModel>? = null
    private lateinit var rvDecks: RecyclerView

    // Filter bar
    private lateinit var rgLanguage: RadioGroup
    private lateinit var etFilter: EditText

    // Import dialog
    private lateinit var fab: ExtendedFloatingActionButton
    private var dialog: Dialog? = null
    private lateinit var btnCancel: Button
    private lateinit var btnImport: Button
    private lateinit var btnSelectFile: ImageButton
    private lateinit var etName: EditText
    private lateinit var etLanguage: EditText
    private lateinit var etFilepath: EditText
    private lateinit var rvPreview: RecyclerView

    // Import data
    private var importedDeckName: String? = null
    private var importedWordList: MutableList<WordModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDecksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Instantiates the database helper
        dbHelper = DatabaseHelper(context)

        // Gets the RecyclerView and sets the content from the database
        rvDecks = root.findViewById(R.id.deck_list)
        refreshRecyclerView(dbHelper.decks)

        // Adds an onClick for the "All" RadioButton
        val rbAll: RadioButton = root.findViewById(R.id.rb_all)
        rbAll.setOnClickListener { refreshRecyclerView(dbHelper.decks) }

        // Creates RadioButtons for the filter bar for all database deck languages
        // TODO: Refactor using RecyclerView
        rgLanguage = root.findViewById(R.id.rg_language)
        val bookLanguages: List<LanguageModel> = dbHelper.deckLanguages
        for (language in bookLanguages) {
            val rbLanguage = RadioButton(context)
            rbLanguage.text = language.code.uppercase()
            rbLanguage.setBackgroundResource(R.drawable.rb_language)
            rbLanguage.setButtonDrawable(android.R.color.transparent)
            rbLanguage.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val colorList = ColorStateList.valueOf(language.color)
            if (rbLanguage.isSelected) { rbLanguage.setTextColor(com.google.android.material.R.attr.backgroundColor) }
            else { rbLanguage.setTextColor(colorList) }
            rbLanguage.setBackgroundTintList(ColorStateList.valueOf(language.color))
            rbLanguage.setBackgroundTintMode(PorterDuff.Mode.SRC_IN)

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
                    dbHelper.getDecks(
                        language.id
                    )
                )
            }

            rbLanguage.setOnCheckedChangeListener { _, selected ->
                if (selected) { rbLanguage.setTextColor(requireContext().resources.getColor(R.color.black)) }
                else { rbLanguage.setTextColor(colorList) }
            }

            rgLanguage.addView(rbLanguage)
        }
        // Hides the languages filter if no decks are imported
        if (bookLanguages.isEmpty()) {
            root.findViewById<View>(R.id.language_filter).visibility = View.GONE
        }

        // Adds on onClick to refresh when a term is searched for
        etFilter = root.findViewById(R.id.et_filter)
        etFilter.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    refreshRecyclerView(dbHelper.getDecks(etFilter.getText().toString()))
                    return true
                }
                return false
            }
        })

        // Creates the dialog
        dialog = Dialog(requireContext())

        // Adds an onClick to setup and open the dialog using the FloatingActionButton
        fab = root.findViewById(R.id.deck_fab)
        fab.setOnClickListener(View.OnClickListener {
            dialog!!.setContentView(R.layout.dialog_deck)
            dialog!!.window!!
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            etName = dialog!!.findViewById(R.id.et_deck_name)
            etLanguage = dialog!!.findViewById(R.id.et_deck_language)
            etFilepath = dialog!!.findViewById(R.id.et_deck_filepath)
            rvPreview = dialog!!.findViewById(R.id.rv_preview)

            btnCancel = dialog!!.findViewById(R.id.btn_cancel)
            btnImport = dialog!!.findViewById(R.id.btn_import)

            // Adds an onClick to close the dialog
            btnCancel.setOnClickListener { dialog!!.dismiss() }

            // Adds an onClick to import the deck
            btnImport.setOnClickListener(View.OnClickListener { // Checks for deck name user input
                val name: String = etName.getText().toString()
                if (name.isNotBlank()) {
                    importedDeckName = name
                }

                // Checks that the language code is valid
                val languageCode: String = etLanguage.getText().toString()
                val language: LanguageModel =
                    dbHelper.getLanguage(languageCode.lowercase(Locale.getDefault()))
                        ?: return@OnClickListener
                val languageId: Int = language.id

                // Adds the book to the database and gets the deck ID for relating words to decks
                val deckId: Long = dbHelper.addDeck(
                    DeckModel(
                        -1,
                        importedDeckName!!,
                        languageId,
                        importedWordList!!.size
                    )
                )

                // Creates a new word entry if necessary and relates it to the deck
                for (word in importedWordList!!) {
                    var wordId: Long
                    val dbWord: WordModel? = dbHelper.getWord(word.word)
                    if (dbWord != null) {
                        wordId = dbWord.id.toLong()
                    } else {
                        word.language = languageId
                        wordId = dbHelper.addWord(word)
                    }
                    dbHelper.addCard(deckId, wordId)
                }

                // Closes the dialog and refreshes the fragment
                dialog!!.dismiss()
                requireActivity().supportFragmentManager.beginTransaction().detach(requireParentFragment())
                    .commitNowAllowingStateLoss()
                requireActivity().supportFragmentManager.beginTransaction().attach(requireParentFragment())
                    .commitAllowingStateLoss()
            })

            // Adds an onClick to open a file chooser
            btnSelectFile = dialog!!.findViewById(R.id.btn_select_file)
            btnSelectFile.setOnClickListener {
                var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.setType("*/*")
                intent = Intent.createChooser(intent, "Choose a file")
                activityResultLauncher.launch(intent)
            }

            // Shows the dialog
            dialog!!.show()
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        // Sets the action bar title
        // TODO: display icon
        (activity as MainActivity?)!!.supportActionBar!!.title = "Flashcards"
        (activity as MainActivity?)!!.supportActionBar!!.setIcon(R.drawable.ic_decks)
    }

    // Adds a listener for when a file is selected
    // TODO: Error handling for unsupported files and text formats
    // TODO: Use a more standard file format such as CSV
    // TODO: Use ML Kit detect to auto-detect languages
    private var activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val uri = data!!.data

                if (dialog != null) {
                    // Reformats the filepath as a deck name
                    val filepath: String = UriUtils.getFilename(requireContext(), uri)

                    // Sets values for use in the database and for editing by the user
                    importedDeckName = filepath.split(".")[0]
                    etName.setHint(importedDeckName)
                    etFilepath.setHint(filepath)

                    importedWordList = ArrayList()
                    val text: String = UriUtils.readTxtUri(requireContext(), uri).toString()

                    // Parses the cards from the plain text file
                    // Should be stored in the format:
                    //      word:translation;next word:next translation
                    val cards = text.split(";")
                    for (cardString in cards) {
                        Log.d("Imported card", cardString)
                        val card = cardString.split(":")
                        if (card.size != 2) { continue }
                        importedWordList!!.add(
                            WordModel(
                                -1,
                                card[0],
                                card[1],
                                -1
                            )
                        )
                    }
                    
                    val previewAdapter = DeckPreviewRecyclerViewAdapter(
                        requireContext(),
                        importedWordList as ArrayList<WordModel>
                    )
                    rvPreview.adapter = previewAdapter
                    rvPreview.layoutManager = LinearLayoutManager(context)

                    // Shows the preview layout and enables the import button
                    dialog!!.findViewById<View>(R.id.import_deck_preview).visibility =
                        View.VISIBLE
                    btnImport.isEnabled = true
                }
            }
        }

    // Defines an onClick for the DeckRecyclerAdapter
    override fun onItemClick(pos: Int) {
        val intent = Intent(activity, StudyActivity::class.java)
        intent.putExtra("NAME", deckModelList!![pos].name)
        intent.putExtra("ID", deckModelList!![pos].id)
        startActivity(intent)
    }

    // Defines a delete onClick for the DeckRecyclerAdapter
    override fun onDeleteClick(pos: Int) {
        // Deletes the deck from the database and refreshes the fragment
        dbHelper.deleteDeck(deckModelList!![pos].id)
        requireActivity().supportFragmentManager.beginTransaction().detach(requireParentFragment())
            .commitNowAllowingStateLoss()
        requireActivity().supportFragmentManager.beginTransaction().attach(requireParentFragment())
            .commitAllowingStateLoss()
    }

    // Populates the RecyclerView with a specified list of DeckModels
    private fun refreshRecyclerView(decks: ArrayList<DeckModel>) {
        deckModelList = decks
        val deckAdapter = DeckRecyclerViewAdapter(
            requireContext(),
            decks,
            this
        )
        rvDecks.setAdapter(deckAdapter)
        val layoutManager = GridLayoutManager(context, 2)
        rvDecks.setLayoutManager(layoutManager)
    }
}