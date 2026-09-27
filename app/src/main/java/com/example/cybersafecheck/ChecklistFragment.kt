package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cybersafecheck.database.RiskAnswerEntity
import com.example.cybersafecheck.databinding.FragmentChecklistBinding
import com.example.cybersafecheck.databinding.ListItemRiskBinding
import kotlinx.coroutines.launch

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: RiskRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        binding.riskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = RiskRepository.get(requireContext())

        loadChecklist()

        // Informational dialog: score summary
        binding.calculateScoreButton.setOnClickListener {
            ScoreDialogFragment().show(childFragmentManager, ScoreDialogFragment.TAG)
        }

        // Destructive-action dialog: confirm before clearing answers
        binding.resetButton.setOnClickListener {
            ResetConfirmDialogFragment().show(childFragmentManager, ResetConfirmDialogFragment.TAG)
        }

        binding.viewHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_checklist_to_history)
        }

        // Listen for "Reset" being confirmed in the dialog
        childFragmentManager.setFragmentResultListener(
            ResetConfirmDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                repository.resetAll()
                loadChecklist()
                Toast.makeText(requireContext(), R.string.checklist_reset_done, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Reads the answers from Room and (re)builds the list. */
    private fun loadChecklist() {
        viewLifecycleOwner.lifecycleScope.launch {
            val items = repository.getAll()
            binding.riskRecyclerView.adapter = RiskAdapter(items.toMutableList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class RiskHolder(val row: ListItemRiskBinding) :
        RecyclerView.ViewHolder(row.root) {

        fun bind(item: RiskAnswerEntity, onToggled: (Boolean) -> Unit) {
            row.riskCategoryBadge.text = item.category.replace("_", " ")
            row.riskQuestion.text = item.question

            row.riskSwitch.setOnCheckedChangeListener(null)
            row.riskSwitch.isChecked = item.isFlagged

            row.riskSwitch.setOnCheckedChangeListener { _, isChecked ->
                onToggled(isChecked)
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.setFlagged(item.itemId, isChecked)
                }
            }

            // Milestone 3: NavController replaces the manual fragment transaction
            row.riskQuestion.setOnClickListener {
                findNavController().navigate(
                    R.id.action_checklist_to_detail,
                    bundleOf(RiskDetailFragment.ARG_ID to item.itemId)
                )
            }
        }
    }

    private inner class RiskAdapter(val list: MutableList<RiskAnswerEntity>) :
        RecyclerView.Adapter<RiskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskHolder {
            val view = ListItemRiskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RiskHolder(view)
        }

        override fun onBindViewHolder(holder: RiskHolder, position: Int) {
            holder.bind(list[position]) { isChecked ->
                // Keep the in-memory copy in sync so recycled rows show the right state
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    list[pos] = list[pos].copy(isFlagged = isChecked)
                }
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
