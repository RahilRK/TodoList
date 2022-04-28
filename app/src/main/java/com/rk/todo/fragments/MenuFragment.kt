package com.rk.todo.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rk.todo.GlobalClass
import com.rk.todo.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    var TAG = "MenuFragment"
    private lateinit var activity: Context

    private lateinit var globalClass: GlobalClass
    private lateinit var binding: FragmentMenuBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        init()

        return binding.root
    }

    fun init() {
        globalClass = GlobalClass.getInstance(activity)
    }
}