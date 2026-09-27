package com.example.cybersafecheck

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Destructive-action confirmation dialog.
 * It does NOT clear anything itself: it only reports "user said yes"
 * back to ChecklistFragment through the Fragment Result API.
 */
class ResetConfirmDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.reset_title)
            .setMessage(R.string.reset_message)
            .setPositiveButton(R.string.reset_confirm) { _, _ ->
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CONFIRMED to true))
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    companion object {
        const val TAG = "ResetConfirmDialog"
        const val REQUEST_KEY = "reset_checklist_request"
        const val RESULT_CONFIRMED = "confirmed"
    }
}
