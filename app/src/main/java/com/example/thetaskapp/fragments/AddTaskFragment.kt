package com.example.thetaskapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.thetaskapp.MainActivity
import com.example.thetaskapp.R
import com.example.thetaskapp.databinding.FragmentAddTaskBinding
import com.example.thetaskapp.model.Task
import com.example.thetaskapp.viewmodel.TaskViewModel

class AddTaskFragment : Fragment(R.layout.fragment_add_task), MenuProvider {

    // View binding for the fragment
    private var addTaskBinding: FragmentAddTaskBinding? = null
    private val binding get() = addTaskBinding!!

    // ViewModel for managing tasks
    private lateinit var taskViewModel: TaskViewModel

    // View for the fragment
    private lateinit var addTaskView: View

    // Creating the view for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating the layout for this fragment
        addTaskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    // After the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adding options menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Getting the ViewModel from MainActivity
        taskViewModel = (activity as MainActivity).taskViewModel

        // Setting the view
        addTaskView = view
    }

    // Function to save the task
    private fun saveTask(view: View){
        val taskTitle = binding.addTaskTitle.text.toString().trim()
        val taskDesc = binding.addNoteDesc.text.toString().trim()

        if(taskTitle.isNotEmpty()){
            val task = Task(0, taskTitle, taskDesc)
            taskViewModel.addTask(task)

            Toast.makeText(addTaskView.context, "Task Saved!", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        }else{
            Toast.makeText(addTaskView.context, "Please enter task title!", Toast.LENGTH_SHORT).show()
        }
    }

    // Creating options menu for the fragment
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_task, menu)
    }

    // Handling menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.saveMenu -> {
                saveTask(addTaskView)
                true
            }
            else -> false
        }
    }

    // Cleaning up view binding
    override fun onDestroy() {
        super.onDestroy()
        addTaskBinding = null
    }
}
