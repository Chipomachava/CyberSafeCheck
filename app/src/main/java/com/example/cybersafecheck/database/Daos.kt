package com.example.cybersafecheck.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RiskDao {
    @Query("SELECT * FROM risk_answers")
    suspend fun getAll(): List<RiskAnswerEntity>

    @Query("SELECT * FROM risk_answers WHERE itemId = :id LIMIT 1")
    suspend fun getById(id: String): RiskAnswerEntity?

    @Query("UPDATE risk_answers SET isFlagged = :flagged WHERE itemId = :id")
    suspend fun updateFlagged(id: String, flagged: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RiskAnswerEntity>)
}

@Dao
interface AssessmentDao {
    @Insert
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Query("SELECT * FROM assessments ORDER BY timestamp DESC")
    suspend fun getAllAssessments(): List<AssessmentEntity>
}