package com.example.cybersafecheck

import com.example.cybersafecheck.database.RiskAnswerEntity
import com.example.cybersafecheck.database.RiskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RiskRepository(private val dao: RiskDao) {

    suspend fun setFlagged(itemId: String, flagged: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFlagged(itemId, flagged)
    }

    suspend fun getById(itemId: String): RiskAnswerEntity? = withContext(Dispatchers.IO) {
        dao.getById(itemId)
    }

    suspend fun getAll(): List<RiskAnswerEntity> = withContext(Dispatchers.IO) {
        val existing = dao.getAll()
        if (existing.isEmpty()) {
            val initial = listOf(
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
            dao.insertAll(initial)
            return@withContext initial
        }
        existing
    }
}