package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cybersafecheck.database.AssessmentEntity
import com.example.cybersafecheck.databinding.FragmentHistoryBinding
import com.example.cybersafecheck.databinding.ListItemAssessmentBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val historyViewModel: HistoryViewModel by viewModels()
    private var searchView: SearchView? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.uiState.collect { state ->
                    binding.historyRecyclerView.adapter = AssessmentAdapter(state.assessments)
                    binding.historyEmptyText.text = getString(
                        if (state.hasAnyAssessments) R.string.history_no_matches
                        else R.string.history_empty
                    )
                    binding.historyEmptyText.visibility =
                        if (state.assessments.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_history, menu)

                val searchItem = menu.findItem(R.id.menu_item_search)
                val searchBox = searchItem.actionView as SearchView
                searchView = searchBox
                searchBox.queryHint = getString(R.string.search_history)

                searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        historyViewModel.setQuery(query.orEmpty())
                        searchBox.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        historyViewModel.setQuery(newText.orEmpty())
                        return true
                    }
                })

                viewLifecycleOwner.lifecycleScope.launch {
                    val savedQuery = PreferencesRepository.get().historySearchQuery.first()
                    if (savedQuery.isNotEmpty() && searchBox.query.isNullOrEmpty()) {
                        searchBox.isIconified = false
                        searchBox.setQuery(savedQuery, false)
                        searchBox.clearFocus()
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_clear -> {
                        searchView?.setQuery("", false)
                        searchView?.isIconified = true
                        historyViewModel.setQuery("")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView = null
        _binding = null
    }

    private inner class AssessmentHolder(val row: ListItemAssessmentBinding) :
        RecyclerView.ViewHolder(row.root) {

        fun bind(assessment: AssessmentEntity) {
            row.assessmentDate.text = dateFormat.format(Date(assessment.timestamp))
            row.assessmentScore.text =
                "${assessment.flaggedCount} of ${assessment.totalCount} habits flagged"

            val percent = if (assessment.totalCount == 0) 0
            else (assessment.flaggedCount * 100) / assessment.totalCount
            row.assessmentPercent.text = "$percent%"
        }
    }

    private inner class AssessmentAdapter(val list: List<AssessmentEntity>) :
        RecyclerView.Adapter<AssessmentHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessmentHolder {
            val view = ListItemAssessmentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return AssessmentHolder(view)
        }

        override fun onBindViewHolder(holder: AssessmentHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int = list.size
    }
}