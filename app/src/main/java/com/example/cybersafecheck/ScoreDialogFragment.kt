package com.example.cybersafecheck

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Informational dialog: shows the flagged count and per-category breakdown,
 * with a "Save to History" button that stores an AssessmentEntity.
 */
class ScoreDialogFragment : DialogFragment() {

    private lateinit var repository: RiskRepository
    private var summary: ScoreSummary? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        repository = RiskRepository.get(requireContext())

        // MaterialAlertDialogBuilder is a subclass of AlertDialog.Builder
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.score_title)
            .setMessage(R.string.score_loading) // replaced once the score loads
            .setPositiveButton(R.string.save_to_history, null) // click handled in onStart
            .setNegativeButton(R.string.close, null)
            .create()

        // Read the answers from Room off the main thread, then fill in the message
        lifecycleScope.launch {
            val result = repository.getScoreSummary()
            summary = result
            dialog.setMessage(result.toDisplayText())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        // Disabled until the score has loaded
        saveButton.isEnabled = summary != null

        // Custom listener so the dialog stays open until the save finishes
        saveButton.setOnClickListener {
            val current = summary ?: return@setOnClickListener
            saveButton.isEnabled = false
            lifecycleScope.launch {
                repository.saveAssessment(current.flagged, current.total)
                Toast.makeText(requireContext(), R.string.assessment_saved, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "ScoreDialog"
    }
}
