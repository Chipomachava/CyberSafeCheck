package com.example.cybersafecheck

import java.util.UUID

data class RiskItem(
    val id: UUID = UUID.randomUUID(),
    val question: String,
    val category: RiskCategory,
    val explanation: String,
    var isFlagged: Boolean = false
)