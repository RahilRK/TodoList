package com.rk.todo.fragments.AddNote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rk.todo.*
import com.rk.todo.Util.Constant
import com.rk.todo.databinding.FragmentAddNoteBinding
import com.rk.todo.NotesModel
import kotlinx.coroutines.*
import java.lang.Exception


class addNoteFragment : Fragment() {

    var TAG = "addNoteFragment"
    private lateinit var activity: Context

    lateinit var globalClass: GlobalClass
    lateinit var binding: FragmentAddNoteBinding
    lateinit var database: Database

    lateinit var viewModel: addNoteViewModel
    lateinit var repository: Repository

    val job = Job()
    val defaultScope = CoroutineScope(Dispatchers.Default + job)
    val ioScope = CoroutineScope(Dispatchers.IO + job)
    val mainScope = CoroutineScope(Dispatchers.Main + job)

    private var savenotes_menu: MenuItem? = null
    private var removenotes_menu: MenuItem? = null

    val args: addNoteFragmentArgs by navArgs()
    var model: NotesModel? = null
    var noteId = 0

    lateinit var toolbar: androidx.appcompat.widget.Toolbar;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = requireContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNoteBinding.inflate(layoutInflater, container, false)

        init()
        setToolbar()
        onClicks()
        setText()

        return binding.root
    }

    fun init() {
        globalClass = GlobalClass.getInstance(activity)
        database = (requireActivity().application as Application).database
        repository = (requireActivity().application as Application).repository
        viewModel = ViewModelProvider(this, addNoteViewModelFactory(repository))
            .get(addNoteViewModel::class.java)
    }

    fun setToolbar() {
        toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
//        toolbar.setNavigationIcon(R.drawable.ic_menu_close_clear_cancel)
        toolbar.title = ""
    }

    fun onClicks() {

        binding.title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                savenotes_menu?.isVisible =
                    !(binding.title.text.isEmpty() && binding.note.text.isEmpty())

                if (noteId > 0) {
                    removenotes_menu?.isVisible =
                        !(binding.title.text.isEmpty() && binding.note.text.isEmpty())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.note.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable?) {
                savenotes_menu?.isVisible =
                    !(binding.title.text.isEmpty() && binding.note.text.isEmpty())

                if (noteId > 0) {
                    removenotes_menu?.isVisible =
                        !(binding.title.text.isEmpty() && binding.note.text.isEmpty())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }
    }

    fun setText() {

        noteId = args.noteId

        if (noteId > 0) {
            viewModel.getNoteDetail(noteId).observe(requireActivity(), {

                if (it != null) {
                    model = it
                    binding.title.setText(model!!.title)
                    binding.note.setText(model!!.description)
                }
            })
        }
    }

    fun addNote() {

        val newNoteDetail = NotesModel(
            id = args.noteId,
            title = binding.title.text.toString(),
            description = binding.note.text.toString()
        )

        val oldNoteDetail = model

        if(newNoteDetail == oldNoteDetail) {
            globalClass.log(TAG,"No changes made!")
            requireActivity().onBackPressed()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {

            val newJob = async {

                addNoteJob()
                globalClass.log(TAG, "newJob is running...")
            }
            newJob.await()
            newJob.invokeOnCompletion {

                lifecycleScope.launch(Dispatchers.Main) {
                    globalClass.log(TAG, "newJob has completed...")
                    (activity as MainActivity).shouldGoBack = true
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    suspend fun addNoteJob() {

        if (noteId <= 0) {

            globalClass.log(TAG, "Add Note")
            viewModel.addNote(
                NotesModel(
                    title = binding.title.text.toString(),
                    description = binding.note.text.toString()
                )
            )
        } else {

            globalClass.log(TAG, "Update Note")
            viewModel.addNote(
                NotesModel(
                    id = noteId,
                    title = binding.title.text.toString(),
                    description = binding.note.text.toString()
                )
            )
        }
    }

    fun deleteNote() {

        lifecycleScope.launch(Dispatchers.IO) {

            val newJob = async {

                deleteNoteJob()
                globalClass.log(TAG, "newJob is running...")
            }
            newJob.await()
            newJob.invokeOnCompletion {

                lifecycleScope.launch(Dispatchers.Main) {
                    globalClass.log(TAG, "newJob has completed...")
                    (activity as MainActivity).shouldGoBack = true
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    suspend fun deleteNoteJob() {

        globalClass.log(TAG, "Delete Note")
        viewModel.deleteNote(model!!.id)
    }

    private fun showRemoveNoteDialogue(title: String, message: String) {
        MaterialAlertDialogBuilder(activity, R.style.RoundShapeTheme)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, which ->

                dialog.dismiss()
                deleteNote()
            }
            .setNeutralButton("Cancel") { dialog, which ->

                dialog.dismiss()
            }
            .show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addnote_optionmenu, menu)
        savenotes_menu = menu.findItem(R.id.savenotes_menu);
        removenotes_menu = menu.findItem(R.id.removenotes_menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        globalClass.log(TAG,"onPrepareOptionsMenu")

        if(binding.title.text.isNotEmpty() || binding.note.text.isNotEmpty()) {

            savenotes_menu?.isVisible = true
        }

        if (noteId > 0) {
            removenotes_menu?.isVisible =
                !(binding.title.text.isEmpty() && binding.note.text.isEmpty())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.savenotes_menu) {

            globalClass.hideKeyboard(requireActivity())
            addNote()
        } else if (id == R.id.removenotes_menu) {

            globalClass.hideKeyboard(requireActivity())
            showRemoveNoteDialogue("Remove Note", "Are you sure you want to remove note?")
        }

        return false
    }

    private var onBackPressReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {

                globalClass.log(TAG, "onBackPressReceiver")

                if (binding.title.text.isEmpty() && binding.note.text.isEmpty()) {

                    if (noteId > 0) {
                        deleteNote()
                    }
                    else {
                        (activity as MainActivity).shouldGoBack = true
                        requireActivity().onBackPressed()
                    }
                }
                else {
                    addNote()
                }

            } catch (e: Exception) {

                val error = Log.getStackTraceString(e)
                globalClass.log(TAG, error)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(activity).registerReceiver(
            onBackPressReceiver, IntentFilter(Constant.onBackPressReceiver)
        )
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(activity).unregisterReceiver(onBackPressReceiver)
    }
}