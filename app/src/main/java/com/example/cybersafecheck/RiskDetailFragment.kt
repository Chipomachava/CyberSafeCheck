package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cybersafecheck.databinding.FragmentRiskDetailBinding
import kotlinx.coroutines.launch

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
        // The Navigation component puts the "item_id" argument into this Bundle
        val id = arguments?.getString(ARG_ID) ?: return
        val repo = RiskRepository.get(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val item = repo.getById(id) ?: return@launch
            binding.detailCategory.text = item.category.replace("_", " ")
            binding.detailQuestion.text = item.question
            binding.detailExplanation.text = item.explanation
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Must match <argument android:name="item_id"> in nav_graph.xml
        const val ARG_ID = "item_id"
    }
}
