package com.example.cybersafecheck

import java.util.UUID

object RiskLab {
    val items = mutableListOf(
        RiskItem(
            question = "I reuse the same password on more than one website or account.",
            category = RiskCategory.PASSWORDS,
            explanation = "Reusing passwords means a single compromised service exposes all your other accounts."
        ),
        RiskItem(
            question = "I do not use two-factor authentication (2FA) where offered.",
            category = RiskCategory.PASSWORDS,
            explanation = "2FA provides a backup safety check even if an adversary obtains your password."
        ),
        RiskItem(
            question = "I accept connection or friend requests from people I have never met.",
            category = RiskCategory.SOCIAL_MEDIA,
            explanation = "Unverified contacts can scrape personal details and stage targeted social engineering scams."
        ),
        RiskItem(
            question = "My social media posts and profile details are entirely public.",
            category = RiskCategory.SOCIAL_MEDIA,
            explanation = "Public data can be harvested to answer personal security verification questions."
        ),
        RiskItem(
            question = "I open delivery or banking link notifications without checking sender details.",
            category = RiskCategory.SCAMS,
            explanation = "Smishing lures closely resemble genuine notifications to harvest login credentials."
        ),
        RiskItem(
            question = "I connect to open public Wi-Fi networks without using a VPN.",
            category = RiskCategory.SCAMS,
            explanation = "Unencrypted networks can allow adversaries on the same router to monitor unencrypted traffic."
        ),
        RiskItem(
            question = "I share real-time location check-ins and photos while away from home.",
            category = RiskCategory.CYBERBULLYING,
            explanation = "Real-time updates broadcast your exact physical location and routine to bad actors."
        ),
        RiskItem(
            question = "I do not monitor or remove unwanted public mentions and tags.",
            category = RiskCategory.CYBERBULLYING,
            explanation = "Unchecked mentions make it easier for online harassment and impersonation to spread."
        )
    )

    fun getItem(id: UUID): RiskItem? = items.find { it.id == id }

    fun updateFlag(id: UUID, isFlagged: Boolean) {
        items.find { it.id == id }?.isFlagged = isFlagged
    }
}