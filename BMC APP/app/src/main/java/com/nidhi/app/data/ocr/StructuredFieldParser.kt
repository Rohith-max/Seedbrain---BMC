package com.nidhi.app.data.ocr

/**
 * Deterministic, regex-based extractor of structured fields from raw OCR text.
 *
 * Satisfies:
 *  - Requirement 12.1  — extracts dates (dd/MM/yyyy, MM/yyyy, yyyy), Aadhaar, PAN, Passport
 *  - Requirement 12.2  — returns a map with canonical label keys, first match wins
 *  - Requirement 12.4  — deterministic: same input always produces same output and key order
 *  - Requirement 12.5  — Aadhaar numbers validated via Verhoeff check-digit algorithm
 */
object StructuredFieldParser {

    // ── Patterns ──────────────────────────────────────────────────────────────

    private val AADHAAR_PATTERN  = Regex("""\b(\d{4}[\s-]?\d{4}[\s-]?\d{4})\b""")
    private val PAN_PATTERN      = Regex("""\b([A-Z]{5}[0-9]{4}[A-Z])\b""")
    private val PASSPORT_PATTERN = Regex("""\b([A-Z][0-9]{7})\b""")

    // Date patterns in priority order
    private val DATE_PATTERNS = listOf(
        Regex("""\b(0?[1-9]|[12]\d|3[01])/(0?[1-9]|1[0-2])/(\d{4})\b"""),
        Regex("""\b(0?[1-9]|1[0-2])/(\d{4})\b"""),
        Regex("""\b(19\d{2}|20\d{2})\b""")
    )

    // Contextual keywords used to classify date labels
    private val EXPIRY_KEYWORDS = setOf("expiry", "expiration", "valid upto", "valid till", "valid until", "expires")
    private val ISSUE_KEYWORDS  = setOf("issue", "issued", "date of issue", "doi")
    private val DOB_KEYWORDS    = setOf("birth", "dob", "date of birth", "born")

    /**
     * Parses [ocrText] and returns a [LinkedHashMap] of label to extracted value.
     * Key insertion order is deterministic (Aadhaar, PAN, Passport, then dates).
     * Calling this method multiple times on the same input always returns the same result.
     */
    fun parse(ocrText: String): Map<String, String> {
        val result = linkedMapOf<String, String>()

        // ── Aadhaar (with Verhoeff check-digit validation) ────────────────────
        AADHAAR_PATTERN.find(ocrText)
            ?.groupValues?.get(1)
            ?.replace(Regex("[\\s-]"), "")
            ?.takeIf { it.length == 12 && verhoeff(it) }
            ?.let { result["Aadhaar"] = it }

        // ── PAN ───────────────────────────────────────────────────────────────
        PAN_PATTERN.find(ocrText)?.groupValues?.get(1)?.let { result["PAN"] = it }

        // ── Passport ──────────────────────────────────────────────────────────
        PASSPORT_PATTERN.find(ocrText)?.groupValues?.get(1)?.let { result["Passport Number"] = it }

        // ── Dates — classified by surrounding keywords ────────────────────────
        val allDateMatches = mutableListOf<Pair<Int, String>>()
        for (pattern in DATE_PATTERNS) {
            pattern.findAll(ocrText).forEach { allDateMatches += it.range.first to it.value }
        }
        val uniqueDates = allDateMatches.sortedBy { it.first }
        val usedLabels  = mutableSetOf<String>()

        for ((start, raw) in uniqueDates) {
            val label = classifyDate(ocrText, start)
            if (label !in usedLabels) {
                result[label] = raw
                usedLabels += label
            }
        }

        return result
    }

    private fun classifyDate(ocrText: String, idx: Int): String {
        val context = ocrText.substring(maxOf(0, idx - 40), idx).lowercase()
        return when {
            EXPIRY_KEYWORDS.any { context.contains(it) } -> "Expiry Date"
            ISSUE_KEYWORDS.any  { context.contains(it) } -> "Issue Date"
            DOB_KEYWORDS.any    { context.contains(it) } -> "Date of Birth"
            else                                          -> "Expiry Date"
        }
    }

    // ── Verhoeff algorithm ────────────────────────────────────────────────────

    private val D = arrayOf(
        intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        intArrayOf(1, 2, 3, 4, 0, 6, 7, 8, 9, 5),
        intArrayOf(2, 3, 4, 0, 1, 7, 8, 9, 5, 6),
        intArrayOf(3, 4, 0, 1, 2, 8, 9, 5, 6, 7),
        intArrayOf(4, 0, 1, 2, 3, 9, 5, 6, 7, 8),
        intArrayOf(5, 9, 8, 7, 6, 0, 4, 3, 2, 1),
        intArrayOf(6, 5, 9, 8, 7, 1, 0, 4, 3, 2),
        intArrayOf(7, 6, 5, 9, 8, 2, 1, 0, 4, 3),
        intArrayOf(8, 7, 6, 5, 9, 3, 2, 1, 0, 4),
        intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
    )

    private val P = arrayOf(
        intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        intArrayOf(1, 5, 7, 6, 2, 8, 3, 0, 9, 4),
        intArrayOf(5, 8, 0, 3, 7, 9, 6, 1, 4, 2),
        intArrayOf(8, 9, 1, 6, 0, 4, 3, 5, 2, 7),
        intArrayOf(9, 4, 5, 3, 1, 2, 6, 8, 7, 0),
        intArrayOf(4, 2, 8, 6, 5, 7, 3, 9, 0, 1),
        intArrayOf(2, 7, 9, 3, 8, 0, 6, 4, 1, 5),
        intArrayOf(7, 0, 4, 6, 9, 1, 3, 2, 5, 8)
    )

    /**
     * Validates that [number] (12 digits) passes the Verhoeff check-digit algorithm.
     */
    fun verhoeff(number: String): Boolean {
        var c = 0
        val reversed = number.reversed()
        for (i in reversed.indices) {
            val digit = reversed[i].digitToIntOrNull() ?: return false
            c = D[c][P[i % 8][digit]]
        }
        return c == 0
    }
}
