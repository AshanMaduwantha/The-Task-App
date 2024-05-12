package com.example.thetaskapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thetaskapp.MainActivity
import com.example.thetaskapp.R
import com.example.thetaskapp.adapter.TaskAdapter
import com.example.thetaskapp.databinding.FragmentHomeBinding
import com.example.thetaskapp.model.Task
import com.example.thetaskapp.viewmodel.TaskViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {

    // View binding for the fragment
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    // ViewModel for managing tasks
    private lateinit var taskViewModel: TaskViewModel

    // Adapter for displaying tasks in RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    // Creating the view for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflating the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // After the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // Adding options menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Getting the ViewModel from MainActivity
        taskViewModel = (activity as MainActivity).taskViewModel

        // Setting up RecyclerView to display tasks
        setupHomeRecycleView()

        // Navigating to AddTaskFragment when FAB is clicked
        binding.addNoteFab.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
    }

    // Update UI based on task list
    private fun updateUI(task: List<Task>?){
        if(task != null){
            if(task.isNotEmpty()){
                binding.emptyTaskImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            }else{
                binding.emptyTaskImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
            }
        }
    }

    // Setting up RecyclerView with adapter and layout manager
    private fun setupHomeRecycleView(){
        taskAdapter = TaskAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = taskAdapter
        }

        // Observing LiveData from ViewModel to update the task list
        activity?.let {
            taskViewModel.getALlTask().observe(viewLifecycleOwner){ task ->
                taskAdapter.differ.submitList(task)
                updateUI(task)
            }
        }
    }

    // Searching for tasks based on query
    private fun searchTask(query: String?){
        val searchQuery = "%$query"

        taskViewModel.searchTask(query).observe(this) { list ->
            taskAdapter.differ.submitList(list)
        }
    }

    // Function called when text is submitted in the search view
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    // Function called when text changes in the search view
    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchTask(newText)
        }
        return true
    }

    // Cleaning up view binding
    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    // Creating options menu for the fragment
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        // Configuring search view in the options menu
        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    // Handling menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}
