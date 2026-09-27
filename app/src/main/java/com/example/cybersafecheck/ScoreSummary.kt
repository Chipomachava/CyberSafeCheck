package com.example.cybersafecheck

/**
 * Result of scoring the checklist.
 * perCategory maps each category name (e.g. "PASSWORDS") to (flagged, total).
 */
data class ScoreSummary(
    val flagged: Int,
    val total: Int,
    val perCategory: Map<String, Pair<Int, Int>>
) {
    /** Builds the text shown in the score dialog. */
    fun toDisplayText(): String {
        val builder = StringBuilder()
        builder.append("Flagged: $flagged / $total\n\n")
        perCategory.forEach { (category, counts) ->
            builder.append("${prettyCategory(category)}: ${counts.first}/${counts.second} flagged\n")
        }
        return builder.toString().trimEnd()
    }

    companion object {
        /** "SOCIAL_MEDIA" -> "Social Media" */
        fun prettyCategory(raw: String): String =
            raw.split("_").joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
    }
}
