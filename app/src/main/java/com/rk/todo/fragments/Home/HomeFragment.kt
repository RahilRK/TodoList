package com.rk.todo.fragments.Home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.rk.todo.databinding.FragmentHomeBinding
import android.view.*
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.rk.todo.*
import com.rk.todo.NotesModel
import homeAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*

class HomeFragment : Fragment(), homeAdapter.homeAdapterOnClick {

    var TAG = "HomeFragment"
    lateinit var activity: Context

    lateinit var globalClass: GlobalClass
    lateinit var binding: FragmentHomeBinding
    lateinit var database: Database

    lateinit var viewModel: homeViewModel
    lateinit var repository: Repository

    val job = Job()
    val mainScope = CoroutineScope(Dispatchers.Main + job)

    lateinit var adapter : homeAdapter
    var arrayList = arrayListOf<NotesModel>()

    var undoNote = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        init()
        setToolbar()
        onClick()
        getData()

        return binding.root
    }

    fun init() {
        globalClass = GlobalClass.getInstance(activity)
        database = (requireActivity().application as Application).database
        repository = (requireActivity().application as Application).repository
        viewModel = ViewModelProvider(this, homeViewModelFactory(repository))
            .get(homeViewModel::class.java)
    }

    fun setToolbar() {
        val toolbar = binding.toolbar
//        toolbar.setNavigationIcon(R.drawable.ic_menu_close_clear_cancel)
        toolbar.title = "Notes"
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    fun onClick() {

        binding.addNoteFab.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToAddNoteFragment(0)
            findNavController().navigate(action)
        }
    }

    fun getData() {

        viewModel.noteList.observe(requireActivity(),{
//            globalClass.log(TAG,"Notes: ${it.size}")
            arrayList = it as ArrayList<NotesModel>
            setAdapter(arrayList)
            enableSwipeToDeleteAndUndo()
        })
    }

    fun setAdapter(arrayList: ArrayList<NotesModel>) {

        val recyclerview = binding.recyclerView
        recyclerview.layoutManager = LinearLayoutManager(context)
        adapter = homeAdapter(activity, arrayList,this)
        recyclerview.adapter = adapter
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(activity) {

            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {

                val position: Int = viewHolder.getAdapterPosition()
                val model = arrayList.get(position)

                globalClass.log(TAG, "onSwiped: $arrayList")

                adapter.removeItem(position)
                showUndoSnakeBar(model,position)
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(recyclerView)
    }

    fun showUndoSnakeBar(model: NotesModel, position: Int) {

        Snackbar.make(
            binding.addNoteFab,
            getString(R.string.removed), Snackbar.LENGTH_LONG)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)

                    if(!undoNote) {
                        globalClass.log(TAG,"onDismissed")
                        removeNote(model)
                    }
                    undoNote = false
                }
            })
            .setAction(getString(R.string.undo)) {

                undoNote = true
                adapter.restoreItem(model,position)
                globalClass.log(TAG, "onUndo: $arrayList")
            }
            .show()
    }

    fun removeNote(model: NotesModel) {

        lifecycleScope.launch(Dispatchers.IO) {

            val newJob = async {

                removeNoteJob(model)
                globalClass.log(TAG, "newJob is running...")
            }
            newJob.await()
            newJob.invokeOnCompletion {

                lifecycleScope.launch(Dispatchers.Main) {
                    globalClass.log(TAG, "newJob has completed...")
                }
            }
        }
    }

    suspend fun removeNoteJob(model: NotesModel) {

        globalClass.log(TAG, "Remove Note")
        viewModel.addNote(
            NotesModel(
                id = model.id,
                title = model.title,
                description = model.description,
                isRemoved = 1
            )
        )
    }

    override fun viewHomeDetail(pos: Int, model: NotesModel) {

        val action = HomeFragmentDirections.actionHomeFragmentToAddNoteFragment(model.id)
        findNavController().navigate(action)
    }
}