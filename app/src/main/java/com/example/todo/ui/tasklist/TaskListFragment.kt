package com.example.todo.ui.tasklist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.R
import com.example.todo.data.model.TaskEntity
import com.example.todo.databinding.FragmentTaskListBinding
import com.example.todo.viewmodel.TaskViewModel
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter

    // --- State ---
    private var allTasks: List<TaskEntity> = emptyList()
    private var searchQuery: String = ""
    private val selectedPriorities = mutableSetOf<String>()
    private var selectedSort = SortMode.TITLE_ASC

    private enum class SortMode { TITLE_ASC, TITLE_DESC, DUE_CLOSEST }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupObservers()
        setupSearchBar()
        setupPriorityChips()
        setupSortButton()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = TaskListAdapter { selectedTask ->
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskDetailFragment(selectedTask.id.toLong())
            findNavController().navigate(action)
        }
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            allTasks = tasks ?: emptyList()
            applyFilters()
        }
    }

    private fun setupSearchBar() {
        val inputLayout = binding.searchInputLayout
        val editSearch = binding.editSearch

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = (s?.toString() ?: "").trim().lowercase(Locale.getDefault())
                applyFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editSearch.setOnFocusChangeListener { _, hasFocus ->
            inputLayout?.isEndIconVisible = hasFocus
        }

        inputLayout?.setEndIconOnClickListener {
            editSearch.clearFocus()
            editSearch.text?.clear()
            inputLayout.isEndIconVisible = false
            adapter.submitList(allTasks)
        }
    }

    private fun setupPriorityChips() {
        binding.chipGroupPriority?.setOnCheckedStateChangeListener { group, _ ->
            selectedPriorities.clear()
            for (id in group.checkedChipIds) {
                val chip = group.findViewById<Chip>(id)
                chip?.text?.toString()?.let {
                    selectedPriorities.add(it.uppercase(Locale.getDefault()))
                }
            }
            applyFilters()
        }
    }

    private fun setupSortButton() {
        binding.btnSort.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menuInflater.inflate(R.menu.menu_sort, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                selectedSort = when (item.itemId) {
                    R.id.menu_sort_title_az -> SortMode.TITLE_ASC
                    R.id.menu_sort_title_za -> SortMode.TITLE_DESC
                    R.id.menu_sort_due_closest -> SortMode.DUE_CLOSEST
                    else -> SortMode.TITLE_ASC
                }
                applyFilters()
                true
            }
            popup.show()
        }
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addEditTaskFragment)
        }
    }

    /** Applies search, filter, and sort all together **/
    private fun applyFilters() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        var filteredList = allTasks

        // --- Priority filter ---
        if (selectedPriorities.isNotEmpty()) {
            filteredList = filteredList.filter { task ->
                val priority = task.priority?.uppercase(Locale.getDefault()) ?: ""
                selectedPriorities.contains(priority)
            }
        }

        // --- Search filter ---
        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { task ->
                task.title?.lowercase(Locale.getDefault())?.contains(searchQuery) == true
            }
        }

        // --- Sort ---
        filteredList = when (selectedSort) {
            SortMode.TITLE_ASC -> filteredList.sortedBy { it.title?.lowercase(Locale.getDefault()) }
            SortMode.TITLE_DESC -> filteredList.sortedByDescending { it.title?.lowercase(Locale.getDefault()) }
            SortMode.DUE_CLOSEST -> filteredList.sortedBy {
                try {
                    dateFormat.parse(it.dueDate?.trim().orEmpty()) ?: Date(Long.MAX_VALUE)
                } catch (_: Exception) {
                    Date(Long.MAX_VALUE)
                }
            }
        }

        adapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
