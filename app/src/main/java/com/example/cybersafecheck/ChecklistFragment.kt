package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cybersafecheck.databinding.FragmentChecklistBinding
import com.example.cybersafecheck.databinding.ListItemRiskBinding

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        binding.riskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.riskRecyclerView.adapter = RiskAdapter(RiskLab.items)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class RiskHolder(val itemBinding: ListItemRiskBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: RiskItem) {
            itemBinding.riskQuestion.text = item.question

            itemBinding.riskSwitch.setOnCheckedChangeListener(null)
            itemBinding.riskSwitch.isChecked = item.isFlagged

            itemBinding.riskSwitch.setOnCheckedChangeListener { _, isChecked ->
                RiskLab.updateFlag(item.id, isChecked)
            }

            itemBinding.riskQuestion.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RiskDetailFragment.newInstance(item.id))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private inner class RiskAdapter(private val items: List<RiskItem>) :
        RecyclerView.Adapter<RiskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskHolder {
            val inflater = LayoutInflater.from(parent.context)
            val rowBinding = ListItemRiskBinding.inflate(inflater, parent, false)
            return RiskHolder(rowBinding)
        }

        override fun onBindViewHolder(holder: RiskHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }
}