package com.example.cybersafecheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cybersafecheck.databinding.FragmentPasswordCheckBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PasswordCheckFragment : Fragment() {

    private var _binding: FragmentPasswordCheckBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val breachRepository = BreachRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkExposureButton.setOnClickListener {
            checkPassword()
        }

        binding.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkPassword()
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPassword() {
        val password = binding.passwordInput.text?.toString().orEmpty()
        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.password_empty)
            return
        }
        binding.passwordLayout.error = null

        val hash = Sha1Util.sha1Hex(password)
        val prefix = hash.substring(0, 5)
        val suffix = hash.substring(5)

        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val rangeBody = breachRepository.fetchRange(prefix)
                val count = findBreachCount(rangeBody, suffix)
                showResult(count)
            } catch (e: IOException) {
                showError()
            } catch (e: HttpException) {
                showError()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun findBreachCount(rangeBody: String, suffix: String): Int {
        val matchingLine = rangeBody.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.substringBefore(":").equals(suffix, ignoreCase = true) }

        return matchingLine?.substringAfter(":")?.trim()?.toIntOrNull() ?: 0
    }

    private fun showLoading(isLoading: Boolean) {
        val b = _binding ?: return
        b.checkProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        b.checkExposureButton.isEnabled = !isLoading
        if (isLoading) {
            b.resultText.visibility = View.GONE
            b.checkExposureButton.text = getString(R.string.checking)
        } else {
            b.checkExposureButton.text = getString(R.string.check_exposure)
        }
    }

    private fun showResult(count: Int) {
        val b = _binding ?: return
        b.resultText.visibility = View.VISIBLE
        if (count > 0) {
            b.resultText.text = getString(R.string.password_found, count)
            b.resultText.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            )
        } else {
            b.resultText.text = getString(R.string.password_not_found)
            b.resultText.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
            )
        }
    }

    private fun showError() {
        val b = _binding ?: return
        b.resultText.visibility = View.VISIBLE
        b.resultText.text = getString(R.string.network_error)
        b.resultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_category))
    }
}