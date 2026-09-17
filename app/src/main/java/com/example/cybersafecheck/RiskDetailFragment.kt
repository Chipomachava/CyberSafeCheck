package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cybersafecheck.database.CyberSafeDatabase
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
        val id = arguments?.getString(ARG_ID) ?: return
        val db = CyberSafeDatabase.getDatabase(requireContext())
        val repo = RiskRepository(db.riskDao())

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
        private const val ARG_ID = "item_id"

        fun newInstance(id: String) = RiskDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_ID, id) }
        }
    }
}