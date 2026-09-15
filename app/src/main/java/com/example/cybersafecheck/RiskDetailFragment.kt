package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cybersafecheck.databinding.FragmentRiskDetailBinding
import java.util.UUID

class RiskDetailFragment : Fragment() {

    private var _binding: FragmentRiskDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemIdString = arguments?.getString(ARG_ITEM_ID) ?: return
        val item = RiskLab.getItem(UUID.fromString(itemIdString)) ?: return

        binding.detailCategory.text = item.category.name.replace("_", " ")
        binding.detailQuestion.text = item.question
        binding.detailExplanation.text = item.explanation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_ID = "arg_item_id"

        fun newInstance(itemId: UUID): RiskDetailFragment {
            return RiskDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, itemId.toString())
                }
            }
        }
    }
}