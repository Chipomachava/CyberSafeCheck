package com.example.cybersafecheck

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ScoreDialogFragment : DialogFragment() {

    private lateinit var repository: RiskRepository
    private var summary: ScoreSummary? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        repository = RiskRepository.get(requireContext())

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.score_title)
            .setMessage(R.string.score_loading)
            .setPositiveButton(R.string.save_to_history, null)
            .setNegativeButton(R.string.close, null)
            .setNeutralButton(R.string.share, null)
            .create()

        lifecycleScope.launch {
            val result = repository.getScoreSummary()
            summary = result
            dialog.setMessage(result.toDisplayText())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = true
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.isEnabled = true
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val shareButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

        saveButton.isEnabled = summary != null
        shareButton.isEnabled = summary != null

        saveButton.setOnClickListener {
            val current = summary ?: return@setOnClickListener
            saveButton.isEnabled = false
            lifecycleScope.launch {
                repository.saveAssessment(current.flagged, current.total)
                Toast.makeText(requireContext(), R.string.assessment_saved, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        shareButton.setOnClickListener {
            val current = summary ?: return@setOnClickListener
            shareScore(current)
        }
    }

    private fun shareScore(score: ScoreSummary) {
        val percent = if (score.total == 0) 0 else (score.flagged * 100) / score.total

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_text, score.flagged, score.total, percent)
            )
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
        }

        val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_chooser))
        startActivity(chooserIntent)
    }

    companion object {
        const val TAG = "ScoreDialog"
    }
}