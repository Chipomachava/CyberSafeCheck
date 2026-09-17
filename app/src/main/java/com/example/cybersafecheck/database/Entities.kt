package com.example.cybersafecheck.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "risk_answers")
data class RiskAnswerEntity(
    @PrimaryKey val itemId: String = UUID.randomUUID().toString(),
    val question: String,
    val category: String,
    val explanation: String,
    val isFlagged: Boolean = false
)

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val flaggedCount: Int,
    val totalCount: Int
)