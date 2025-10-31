package com.example.todo.ui.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.databinding.FragmentTaskListBinding
import com.example.todo.viewmodel.TaskViewModel
import com.example.todo.R

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = TaskListAdapter { selectedTask ->
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskDetailFragment(selectedTask.id.toLong())
            findNavController().navigate(action)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }

        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addEditTaskFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
