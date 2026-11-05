package com.example.cybersafecheck

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cybersafecheck.database.AssessmentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryUiState(
    val assessments: List<AssessmentEntity> = emptyList(),
    val searchQuery: String = "",
    val hasAnyAssessments: Boolean = false
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val riskRepository = RiskRepository.get(application)
    private val preferencesRepository = PreferencesRepository.get()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val allAssessments = riskRepository.getAllAssessments()
            preferencesRepository.historySearchQuery.collectLatest { query ->
                _uiState.update { oldState ->
                    oldState.copy(
                        assessments = filterAssessments(allAssessments, query),
                        searchQuery = query,
                        hasAnyAssessments = allAssessments.isNotEmpty()
                    )
                }
            }
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            preferencesRepository.setHistorySearchQuery(query)
        }
    }

    private fun filterAssessments(
        assessments: List<AssessmentEntity>,
        query: String
    ): List<AssessmentEntity> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return assessments

        return assessments.filter { assessment ->
            val dateText = dateFormat.format(Date(assessment.timestamp))
            val scoreText = "${assessment.flaggedCount} of ${assessment.totalCount}"
            val percentText = "${percentOf(assessment)}%"

            dateText.contains(trimmed, ignoreCase = true) ||
                    scoreText.contains(trimmed, ignoreCase = true) ||
                    percentText.contains(trimmed)
        }
    }

    private fun percentOf(assessment: AssessmentEntity): Int {
        return if (assessment.totalCount == 0) 0
        else (assessment.flaggedCount * 100) / assessment.totalCount
    }
}