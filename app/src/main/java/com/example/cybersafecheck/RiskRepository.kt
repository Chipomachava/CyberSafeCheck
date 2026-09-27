package com.example.cybersafecheck

import android.content.Context
import com.example.cybersafecheck.database.AssessmentDao
import com.example.cybersafecheck.database.AssessmentEntity
import com.example.cybersafecheck.database.CyberSafeDatabase
import com.example.cybersafecheck.database.RiskAnswerEntity
import com.example.cybersafecheck.database.RiskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RiskRepository(
    private val riskDao: RiskDao,
    private val assessmentDao: AssessmentDao
) {

    // ---------- Risk answers ----------

    suspend fun setFlagged(itemId: String, flagged: Boolean) = withContext(Dispatchers.IO) {
        riskDao.updateFlagged(itemId, flagged)
    }

    suspend fun getById(itemId: String): RiskAnswerEntity? = withContext(Dispatchers.IO) {
        riskDao.getById(itemId)
    }

    suspend fun getAll(): List<RiskAnswerEntity> = withContext(Dispatchers.IO) {
        val existing = riskDao.getAll()
        if (existing.isEmpty()) {
            val initial = seedItems()
            riskDao.insertAll(initial)
            return@withContext initial
        }
        existing
    }

    /** Milestone 3: clears every yes/no answer back to "not flagged". */
    suspend fun resetAll() = withContext(Dispatchers.IO) {
        riskDao.resetAll()
    }

    /** Milestone 3: counts flagged answers overall and per category. */
    suspend fun getScoreSummary(): ScoreSummary = withContext(Dispatchers.IO) {
        val items = getAll()
        val perCategory = linkedMapOf<String, Pair<Int, Int>>()

        // Walk the enum so categories always appear in the same order
        RiskCategory.values().forEach { category ->
            val inCategory = items.filter { it.category == category.name }
            if (inCategory.isNotEmpty()) {
                perCategory[category.name] =
                    inCategory.count { it.isFlagged } to inCategory.size
            }
        }

        ScoreSummary(
            flagged = items.count { it.isFlagged },
            total = items.size,
            perCategory = perCategory
        )
    }

    // ---------- Assessment history ----------

    suspend fun saveAssessment(flagged: Int, total: Int) = withContext(Dispatchers.IO) {
        assessmentDao.insertAssessment(
            AssessmentEntity(flaggedCount = flagged, totalCount = total)
        )
    }

    suspend fun getAllAssessments(): List<AssessmentEntity> = withContext(Dispatchers.IO) {
        assessmentDao.getAllAssessments()
    }

    // ---------- Seed data ----------

    private fun seedItems() = listOf(
        RiskAnswerEntity(
            question = "I reuse the same password on more than one website or account.",
            category = "PASSWORDS",
            explanation = "Reusing passwords means a single compromised service exposes all your other accounts."
        ),
        RiskAnswerEntity(
            question = "I do not use two-factor authentication (2FA) where offered.",
            category = "PASSWORDS",
            explanation = "2FA provides a backup safety check even if an adversary obtains your password."
        ),
        RiskAnswerEntity(
            question = "I accept connection or friend requests from people I have never met.",
            category = "SOCIAL_MEDIA",
            explanation = "Unverified contacts can scrape personal details and stage targeted social engineering scams."
        ),
        RiskAnswerEntity(
            question = "My social media posts and profile details are entirely public.",
            category = "SOCIAL_MEDIA",
            explanation = "Public data can be harvested to answer personal security verification questions."
        ),
        RiskAnswerEntity(
            question = "I open delivery or banking link notifications without checking sender details.",
            category = "SCAMS",
            explanation = "Smishing lures closely resemble genuine notifications to harvest login credentials."
        ),
        RiskAnswerEntity(
            question = "I connect to open public Wi-Fi networks without using a VPN.",
            category = "SCAMS",
            explanation = "Unencrypted networks can allow adversaries on the same router to monitor unencrypted traffic."
        ),
        RiskAnswerEntity(
            question = "I share real-time location check-ins and photos while away from home.",
            category = "CYBERBULLYING",
            explanation = "Real-time updates broadcast your exact physical location and routine to bad actors."
        ),
        RiskAnswerEntity(
            question = "I do not monitor or remove unwanted public mentions and tags.",
            category = "CYBERBULLYING",
            explanation = "Unchecked mentions make it easier for online harassment and impersonation to spread."
        )
    )

    companion object {
        /** One-line way for any fragment to get a repository. */
        fun get(context: Context): RiskRepository {
            val db = CyberSafeDatabase.getDatabase(context)
            return RiskRepository(db.riskDao(), db.assessmentDao())
        }
    }
}
