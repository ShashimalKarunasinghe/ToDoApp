package com.example.todo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.data.model.TaskEntity
import com.example.todo.databinding.FragmentTaskDetailBinding
import com.example.todo.viewmodel.TaskViewModel

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    private var currentTask: TaskEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId = args.taskId

        // Observe and display task details
        viewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
            currentTask = task
            task?.let {
                binding.textTitle?.text = it.title
                binding.textDescription?.text = it.description
                binding.textDateTime?.text = it.dueDate ?: ""
                binding.textPriority?.text = getString(R.string.priority_label, it.priority)
                binding.sliderProgress?.value = it.progress.toFloat()
                binding.checkCompleted?.isChecked = it.isCompleted
            }
        }

        // Handle slider and buttons
        binding.sliderProgress?.addOnChangeListener { _, _, fromUser ->
            if (fromUser) {
                binding.btnUpdateProgress?.visibility = View.VISIBLE
            }
        }

        binding.btnUpdateProgress?.setOnClickListener {
            currentTask?.let { task ->
                val updatedTask = task.copy(progress = binding.sliderProgress?.value?.toInt() ?: 0)
                viewModel.updateTask(updatedTask)
                Toast.makeText(requireContext(), R.string.progress_updated, Toast.LENGTH_SHORT).show()
                binding.btnUpdateProgress?.visibility = View.GONE
            }
        }

        binding.btnEdit?.setOnClickListener {
            findNavController().navigate(
                TaskDetailFragmentDirections.actionTaskDetailFragmentToAddEditTaskFragment(args.taskId)
            )
        }

        binding.btnDelete?.setOnClickListener {
            currentTask?.let {
                viewModel.deleteTask(it)
                Toast.makeText(requireContext(), R.string.task_deleted, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
