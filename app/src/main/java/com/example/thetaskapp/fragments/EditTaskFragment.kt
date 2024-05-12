package com.example.thetaskapp.fragments

import android.app.AlertDialog
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
import androidx.navigation.fragment.navArgs
import com.example.thetaskapp.MainActivity
import com.example.thetaskapp.R
import com.example.thetaskapp.databinding.FragmentEditTaskBinding
import com.example.thetaskapp.model.Task
import com.example.thetaskapp.viewmodel.TaskViewModel

class EditTaskFragment : Fragment(R.layout.fragment_edit_task), MenuProvider {

    // View binding for the fragment
    private var editTaskBinding: FragmentEditTaskBinding? = null
    private val binding get() = editTaskBinding!!

    // ViewModel for managing tasks
    private lateinit var taskViewModel: TaskViewModel

    // Current task to be edited
    private lateinit var currentTask: Task

    private var selectedPriority: Int = -1

    // Arguments passed to the fragment
    private val args: EditTaskFragmentArgs by navArgs()

    // Creating the view for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflating the layout for this fragment
        editTaskBinding = FragmentEditTaskBinding.inflate(inflater, container, false)
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

        // Getting the current task to be edited from arguments
        currentTask = args.task!!

        // Setting task details to EditText fields
        binding.editTaskTitle.setText(currentTask.taskTitle)
        binding.editTaskDesc.setText(currentTask.taskDesc)

        when (currentTask.priority) {
            1 -> binding.highPriorityRadioButton.isChecked = true
            2 -> binding.mediumPriorityRadioButton.isChecked = true
            3 -> binding.lowPriorityRadioButton.isChecked = true
            else -> {-1}
        }

        binding.priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPriority = when (checkedId) {
                R.id.highPriorityRadioButton -> 1
                R.id.mediumPriorityRadioButton -> 2
                R.id.lowPriorityRadioButton -> 3
                else -> {-1}
            }
        }

        // Updating task when FAB is clicked
        binding.editTaskFab.setOnClickListener{
            val taskTitle = binding.editTaskTitle.text.toString().trim()
            val taskDesc = binding.editTaskDesc.text.toString().trim()

            if(taskTitle.isNotEmpty()){
                val task = Task(currentTask.id, taskTitle, taskDesc, selectedPriority)
                taskViewModel.updateTask(task)
                Toast.makeText(context, "Task Changed!", Toast.LENGTH_SHORT).show()
                view.findNavController().popBackStack(R.id.homeFragment, false)
            }else{
                Toast.makeText(context, "Please enter title!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to delete the task
    private fun deleteTask(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Task!")
            setMessage("Do you want to delete this task?")
            setPositiveButton("Delete"){_,_  ->
                taskViewModel.deleteTask(currentTask)
                Toast.makeText(context, "Task Deleted!", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    // Creating options menu for the fragment
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_task, menu)
    }

    // Handling menu item selection
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteTask()
                true
            }
            else -> false
        }
    }

    // Cleaning up view binding
    override fun onDestroy() {
        super.onDestroy()
        editTaskBinding = null
    }
}
