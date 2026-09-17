package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cybersafecheck.database.CyberSafeDatabase
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

        val db = CyberSafeDatabase.getDatabase(requireContext())
        repository = RiskRepository(db.riskDao())

        viewLifecycleOwner.lifecycleScope.launch {
            val items = repository.getAll()
            binding.riskRecyclerView.adapter = RiskAdapter(items)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class RiskHolder(val row: ListItemRiskBinding) :
        RecyclerView.ViewHolder(row.root) {

        fun bind(item: RiskAnswerEntity) {
            row.riskCategoryBadge.text = item.category.replace("_", " ")
            row.riskQuestion.text = item.question

            row.riskSwitch.setOnCheckedChangeListener(null)
            row.riskSwitch.isChecked = item.isFlagged

            row.riskSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.setFlagged(item.itemId, isChecked)
                }
            }

            row.riskQuestion.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RiskDetailFragment.newInstance(item.itemId))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private inner class RiskAdapter(val list: List<RiskAnswerEntity>) :
        RecyclerView.Adapter<RiskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskHolder {
            val view = ListItemRiskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RiskHolder(view)
        }

        override fun onBindViewHolder(holder: RiskHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int = list.size
    }
}