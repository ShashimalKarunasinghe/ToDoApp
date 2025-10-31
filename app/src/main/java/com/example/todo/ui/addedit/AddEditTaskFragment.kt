package com.example.todo.ui.addedit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.databinding.FragmentAddEditTaskBinding
import com.example.todo.viewmodel.TaskViewModel
import com.example.todo.data.model.TaskEntity
import java.util.Calendar

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private val args: AddEditTaskFragmentArgs by navArgs()

    private var selectedPriority = "Medium"
    private var selectedDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Priority dropdown setup
        val priorities = listOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, priorities)
        binding.spinnerPriority?.setAdapter(adapter)
        binding.spinnerPriority?.setOnClickListener { binding.spinnerPriority!!.showDropDown() }

        // Date picker setup
        binding.editTextDate?.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val formatted = "$dayOfMonth/${month + 1}/$year"
                    binding.editTextDate?.setText(formatted)
                    selectedDate = formatted
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Load existing task if editing
        if (args.taskId != -1L) {
            viewModel.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
                task?.let {
                    binding.editTextTitle?.setText(it.title)
                    binding.editTextDescription?.setText(it.description)
                    binding.editTextDate?.setText(it.dueDate)
                    selectedPriority = it.priority
                    binding.spinnerPriority?.setText(it.priority, false)
                    binding.btnSave.text = getString(R.string.update_task)
                }
            }
        }

        // Save button
        binding.btnSave.setOnClickListener {
            val title = binding.editTextTitle?.text.toString().trim()
            val description = binding.editTextDescription?.text.toString().trim()
            val date = binding.editTextDate?.text.toString().trim()
            val priority = binding.spinnerPriority?.text.toString().trim().ifEmpty { "Medium" }

            if (title.isNotEmpty()) {
                val task = TaskEntity(
                    id = if (args.taskId != -1L) args.taskId else 0L,
                    title = title,
                    description = description,
                    dueDate = date,
                    priority = priority,
                    isCompleted = false,
                    progress = 0
                )

                if (args.taskId != -1L)
                    viewModel.updateTask(task)
                else
                    viewModel.insertTask(task)

                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
