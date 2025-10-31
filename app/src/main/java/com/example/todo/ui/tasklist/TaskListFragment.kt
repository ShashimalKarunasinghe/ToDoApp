package com.example.todo.ui.tasklist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.google.android.material.chip.Chip
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.R
import com.example.todo.databinding.FragmentTaskListBinding
import com.example.todo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter

    // State
    private var allTasks = listOf<com.example.todo.data.model.TaskEntity>()
    private var searchQuery = ""
    private val selectedPriorities = mutableSetOf<String>()
    private var selectedSort = SortMode.TITLE_ASC

    private enum class SortMode { TITLE_ASC, TITLE_DESC, DUE_CLOSEST }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TaskListAdapter { selectedTask ->
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskDetailFragment(selectedTask.id.toLong())
            findNavController().navigate(action)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = adapter

        // --- Observe tasks ---
        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            allTasks = tasks ?: emptyList()
            applyFilters()
        }

        // --- Search logic ---
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = (s?.toString() ?: "").trim().lowercase(Locale.getDefault())
                applyFilters()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // --- Clear icon behavior ---
        binding.searchInputLayout?.isEndIconVisible = false
        binding.editSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.searchInputLayout?.isEndIconVisible =
                hasFocus && binding.editSearch.text?.isNotEmpty() == true
        }
        binding.searchInputLayout?.setEndIconOnClickListener {
            binding.editSearch.text?.clear()
            binding.editSearch.clearFocus()
            binding.searchInputLayout!!.isEndIconVisible = false
            searchQuery = ""
            applyFilters()
        }

        // --- Priority Chips ---
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

        // --- Sort Button (Popup Menu) ---
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

        // --- FAB ---
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addEditTaskFragment)
        }
    }

    /** Applies search, filter, and sort together **/
    private fun applyFilters() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var list = allTasks

        // --- Priority filter ---
        if (selectedPriorities.isNotEmpty()) {
            list = list.filter { task ->
                val p = task.priority?.uppercase(Locale.getDefault()) ?: ""
                selectedPriorities.contains(p)
            }
        }

        // --- Search filter ---
        if (searchQuery.isNotEmpty()) {
            list = list.filter { task ->
                task.title?.lowercase(Locale.getDefault())?.contains(searchQuery) == true
            }
        }

        // --- Sort ---
        list = when (selectedSort) {
            SortMode.TITLE_ASC -> list.sortedBy { it.title?.lowercase(Locale.getDefault()) ?: "" }
            SortMode.TITLE_DESC -> list.sortedByDescending { it.title?.lowercase(Locale.getDefault()) ?: "" }
            SortMode.DUE_CLOSEST -> list.sortedBy {
                val raw = taskDateString(it)
                try {
                    dateFormat.parse(raw) ?: Date(Long.MAX_VALUE)
                } catch (_: Exception) {
                    Date(Long.MAX_VALUE)
                }
            }
        }

        adapter.submitList(list)
    }

    private fun taskDateString(task: com.example.todo.data.model.TaskEntity): String {
        return task.dueDate?.trim().orEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
