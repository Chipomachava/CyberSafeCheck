package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cybersafecheck.database.AssessmentEntity
import com.example.cybersafecheck.databinding.FragmentHistoryBinding
import com.example.cybersafecheck.databinding.ListItemAssessmentBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

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
        val repository = RiskRepository.get(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val assessments = repository.getAllAssessments() // newest first
            binding.historyRecyclerView.adapter = AssessmentAdapter(assessments)
            binding.historyEmptyText.visibility =
                if (assessments.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
